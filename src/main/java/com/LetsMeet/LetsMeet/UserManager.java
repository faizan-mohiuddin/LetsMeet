package com.LetsMeet.LetsMeet;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.UUID;

public class UserManager {

    public UserManager(){

    }

    public static UUID createUserUUID(String fName, String lName, String email){
        String uuidData = fName + lName + email;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }

    public byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public byte[] generateHash(String password, byte[] salt){
        //PBKDF2 hashing
        //https://www.baeldung.com/java-password-hashing

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return hash;
        } catch(Exception e) {
            System.out.println(e);
            return null;
        }

    }

}
