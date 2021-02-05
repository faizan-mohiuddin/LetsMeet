package com.LetsMeet.LetsMeet.User.Service;

import com.LetsMeet.LetsMeet.User.DAO.UserDao;
import com.LetsMeet.LetsMeet.User.Model.User_Internal;
import com.LetsMeet.Models.TokenData;
import com.LetsMeet.Models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.time.Instant;

import static com.LetsMeet.LetsMeet.User.Service.UserService.fromHex;

@Service
public class ValidationService {

    @Autowired
    UserDao dao = new UserDao();

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
            TokenData tokenData = dao.getTokenRecord(token);

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

    public User_Internal getUserFromToken(String token){
        String uuid = this.getUserUUIDfromToken(token);
        return dao.getUserByUUID(uuid);
    }

    public String getUserUUIDfromToken(String token){
        return dao.getUserUUIDByToken(token);
    }

    public User_Internal validate(String email, String password){
        // Get user corresponding to email
        User_Internal user = dao.getUserByEmail(email);

        // Check if password is correct
        boolean match = validatePassword(password, user);

        if(match) {
            return user;
        }else{
            return null;
        }
    }

    private boolean validatePassword(String password, User_Internal user){
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
