package com.LetsMeet.LetsMeet;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
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

    public byte[] generateHash(String password, byte[] salt){
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
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

}
