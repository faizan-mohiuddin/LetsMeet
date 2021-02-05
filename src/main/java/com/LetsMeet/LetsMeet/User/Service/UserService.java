package com.LetsMeet.LetsMeet.User.Service;
import com.LetsMeet.LetsMeet.User.DAO.*;
import com.LetsMeet.LetsMeet.User.Model.UserSanitised;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import com.LetsMeet.LetsMeet.User.Model.Token;
import com.LetsMeet.LetsMeet.User.Model.User;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    UserDao dao;
    
    //@Autowired
    //TokenDAO tokenDao;

	@Override
	public String createUser(String fName, String lName, String email, String password) {
	    // Check that email has not already been used
        boolean uniqueEmail = checkUniqueEmail(email);

        if(!uniqueEmail){
            return "Email address is already used for another account.";
        }

	    // Generate UserUUID, passwordHash, salt
        UUID UserUUID = createUserUUID(fName, lName, email); // UUID for new user
        byte[] salt = generateSalt();
        byte[] hash = generateHash(password, salt);

        if(hash == null){
            return "An error occurred creating account";
        }

	    // Create User object for internal use
        User newUser = new User(UserUUID, fName, lName, email, toHex(hash), toHex(salt));
        password = null; // For security

        // Do validation and security business
        int r = dao.save(newUser);

        if(r > 0){
            return "User successfully created";
        }else{
            return "Error creating user";
        }
	}

    @Override
    public void updateUser(String uuid, User user) {
        dao.update(user);

    }

    @Override
    public String deleteUser(User user) {
        return dao.delete(user);
    }

    @Override
    public Collection<User> getUsers() {
        System.out.println("hey from userservice.java!");
        return dao.getAll();
    }

    @Override
    public UserSanitised getSantitised(User user) {  
        return new UserSanitised(user.getfName(), user.getlName(), user.getEmail());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static UUID createUserUUID(String fName, String lName, String email){
        String uuidData = fName + lName + email;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }

    public static byte[] generateSalt(){
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            return salt;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }

    public static byte[] generateHash(String password, byte[] salt){
        //PBKDF2 hashing
        //https://howtodoinjava.com/java/java-security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 64 * 8);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return hash;
        } catch(Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static String toHex(byte[] arr){
        BigInteger bi = new BigInteger(1, arr);
        String hex = bi.toString(16);
        int paddingLength = (arr.length * 2) - hex.length();
        if(paddingLength > 0){
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    public static byte[] fromHex(String hex){
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public boolean checkUniqueEmail(String email){
        // Returns true if the given email is not already in the DB
        // Otherwise returns false
        //TODO logic should not reside with in dao, get dao to returnr user by email.
        return !dao.checkEmailExists(email);
    }

    public String getUserToken(User user){
        // User needs new token issued
        // Check if user currently has a token issued
        // If they do, remove it and issue a new one
        if(dao.CheckUserToken(user.getStringUUID())){
            // Remove tokens
            dao.removeAllUserToken(user.getStringUUID());
        }

        // Create new token
        String token = this.createAPItoken(user.getStringUUID(), user.getfName(), user.getlName(),
                user.getEmail(), user.getSalt());

        // Add to DB
        long tokenExpires = Instant.now().getEpochSecond() + 3600;  // Token expires an hour from when it was created
        String feedback = dao.createToken(user.getStringUUID(), token, tokenExpires);

        if(feedback.equals("Token created successfully")) {
            return token;
        }else{
            return feedback;
        }
    }

    public static String createAPItoken(String UserUUID, String fName, String lName, String email, String salt) {
        // Add time to token data
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String tokenData = UserUUID + fName + lName + email + strTime;
        String tokenUUID = UUID.nameUUIDFromBytes(tokenData.getBytes()).toString().replace("-", "");

        // Hash the token
        byte[] tokenByte = generateHash(tokenUUID, fromHex(salt));

        String token = toHex(tokenByte);
        return token;
    }

    public User getUserByUUID(String uuid){
	    return dao.getUserByUUID(uuid);
    }


}
