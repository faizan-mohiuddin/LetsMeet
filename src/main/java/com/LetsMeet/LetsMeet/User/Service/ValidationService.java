package com.LetsMeet.LetsMeet.User.Service;

import com.LetsMeet.LetsMeet.User.DAO.TokenDAO;
import com.LetsMeet.LetsMeet.User.DAO.UserDao;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.Models.Data.TokenData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.*;

import static com.LetsMeet.LetsMeet.User.Service.UserService.fromHex;

@Service
public class ValidationService {

    @Autowired
    UserDao dao;

    @Autowired
    TokenDAO tokenDao;

    // For checking email syntax
    private static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    public User getAuthenticatedUser(String token) throws IllegalArgumentException{
        Object[] response = verifyAPItoken(token);
        if ((boolean) response[0]){
            return getUserFromToken(token);
        }
        else{
            throw new IllegalArgumentException("Authentication failed: invalid token");
        }
    }

    public Object[] verifyAPItoken(String token){
        // Returns [boolean, String]
        Object[] arr = new Object[2];

        // Check that token is not null
        if(token.equals("")){
            arr[0] = false;
            arr[1] = "API token required";
        }else{
            // rest of checks
            // Get record from DB
            TokenData tokenData = tokenDao.getTokenRecord(token);

            if(tokenData == null){
                arr[0] = false;
                arr[1] = "Token not recognised. Please login to receive a new token";
            }else{
                // Check token expiry
                long currentTime = Instant.now().getEpochSecond();
                if(currentTime <= tokenData.getExpires()){
                    arr[0] = true;
                    arr[1] = "";
                }else{
                    arr[0] = false;
                    arr[1] = "Token has expired. Please login to receive a new token";
                }
            }
        }

        return arr;
    }

    public User getUserFromToken(String token){
        String uuid = this.getUserUUIDfromToken(token);

        return dao.get(UUID.fromString(uuid)).get();
    }

    public String getUserUUIDfromToken(String token){
        return tokenDao.getUserUUIDByToken(token);
    }

    public User validate(String email, String password){
        // Get user corresponding to email
        Optional<User> user = dao.get(email);
        boolean valid = user.isPresent();

        if(!valid){
            return null;
        }

        // Check if password is correct
        boolean match = validatePassword(password, user.get());

        if(match) {
            return user.get();
        }else{
            return null;
        }
    }

    public Object[] checkEmailValidity(String email){
        // Returns [boolean, String]
        Object[] arr = new Object[2];

        // Check email is not already in use
        if(!dao.get(email).isPresent()){
            // Check email is comprised of correct parts
            Pattern pattern = Pattern.compile(EMAIL_REGEX);
            Matcher matcher = pattern.matcher(email);
            boolean result = matcher.matches();
            arr[0] = result;
            if(result){
                arr[1] = "";
            }else{
                arr[1] = "Email is not valid";
            }
            return arr;
        }
        arr[0] = false;
        arr[1] = "Email already in use";
        return arr;
    }

    // Private methods
    private boolean validatePassword(String password, User user){
        if(user == null){
            return false;
        }

        String passwordHash = user.getPasswordHash();
        String HexSalt = user.getSalt();

        int iterations = 65536;
        byte[] hash = fromHex(passwordHash);
        byte[] salt = fromHex(HexSalt);

        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            int diff = hash.length ^ testHash.length;
            for(int i = 0; i < hash.length && i < testHash.length; i++)
            {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;

        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

}
