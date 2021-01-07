package com.LetsMeet.LetsMeet;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.UUID;
import java.time.Instant;

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

    public static boolean validatePassword(String password, String passwordHash, String HexSalt){
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
    
    public static String createAPItoken(String UserUUID, String fName, String lName, String email, String salt){
        // Add time to token data
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);

        String tokenData = UserUUID + fName + lName + email + strTime;
        String tokenUUID = UUID.nameUUIDFromBytes(tokenData.getBytes()).toString().replace("-", "");

        // Hash the token
        byte[] tokenByte = UserManager.generateHash(tokenUUID, UserManager.fromHex(salt));

        String token = UserManager.toHex(tokenByte);
        return token;
    }

}
