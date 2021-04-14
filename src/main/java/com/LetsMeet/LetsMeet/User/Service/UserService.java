//-----------------------------------------------------------------
// UserService.java
// Let's Meet 2021
//
// Responsible for performing high level BL on User objects

package com.LetsMeet.LetsMeet.User.Service;

//-----------------------------------------------------------------

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.DAO.*;
import com.LetsMeet.LetsMeet.User.Model.IsGuest;
import com.LetsMeet.LetsMeet.User.Model.UserSanitised;
import com.LetsMeet.LetsMeet.User.Model.Token;
import com.LetsMeet.LetsMeet.User.Model.User;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

//-----------------------------------------------------------------

@Service
public class UserService implements UserServiceInterface {

    // Components
    //-----------------------------------------------------------------

    // Interface with User persistant storage
    @Autowired
    UserDao dao;
    
    // Interface with Token persitant storage
    @Autowired
    TokenDAO tokenDao;

    @Autowired
    EventService eventService;

    @Autowired
    BusinessService businessService;

    @Autowired
    ValidationService validationService;

    @Autowired
    GuestDAO guestDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    // CRUD
    //-----------------------------------------------------------------

    // Create user
	@Override
	public String createUser(String fName, String lName, String email, String password) {
	    // Check that email has not already been used
        User u = this.getUserByEmail(email);

        if(u == null) {
            // Generate UserUUID, passwordHash, salt
            UUID UserUUID = createUserUUID(fName, lName, email); // UUID for new user
            byte[] salt = generateSalt();
            byte[] hash = generateHash(password, salt);

            if (hash == null) {
                return "An error occurred creating account";
            }

            // Create User object for internal use
            User newUser = new User(UserUUID, fName, lName, email, toHex(hash), toHex(salt));
            password = null; // For security

            // Do validation and security business
            if (Boolean.TRUE.equals(dao.save(newUser))) {
                return "User Account Created Successfully";
            } else {
                LOGGER.error("An error occurred creating account");
                return "An error occurred creating account";
            }

        }else{
            // User exists
            if(u.getIsGuest()){
                // User is guest
                this.updateUser(u, fName, lName, "", password, false);
                return "Account upgraded from guest account to full account";
            }else{
                // email is already in use
                return "Email address is already used for another account.";
            }
        }
	}

    public String updateUser(User user, String fName, String lName, String email) {

	    // Check if email needs updated
        if(!email.equals("")){
            // Check email is valid
            Object[] valid = validationService.checkEmailValidity(email);
            if(!(boolean) valid[0]){
                return (String) valid[1];
            }
        }

        // Populate user object with updated values
        user.switchFName(fName);
        user.switchLName(lName);
        user.switchEmail(email);


        // Update in DB
        if(dao.update(user)){
            return "User successfully updated";
        }
        return "Error updating user";
    }

    public String updateUser(User user, String fName, String lName, String email, String pw, Boolean isGuest) {

        // Check if email needs updated
        if(!email.equals("")){
            // Check email is valid
            Object[] valid = validationService.checkEmailValidity(email);
            if(!(boolean) valid[0]){
                return (String) valid[1];
            }
        }

        // Populate user object with updated values
        user.switchFName(fName);
        user.switchLName(lName);
        user.switchEmail(email);
        user.switchIsGuestStatus(isGuest);

        // New password means new salt
        if(!(pw.equals(""))){
            byte[] salt = generateSalt();
            byte[] hash = generateHash(pw, salt);

            if (hash == null) {
                return "An error occurred updating account";
            }

            user.setPWHash(toHex(hash));
            user.setSalt(toHex(salt));
        }

        // Update in DB
        if(dao.update(user)){
            return "User successfully updated";
        }

        return "Error updating user";
    }

    @Override
    public String deleteUser(User user) {
        Collection<Event> events = eventService.getUserEvents(user);

        for(Event e : events){
            if(eventService.checkOwner(e.getUUID(), user.getUUID())) {
                eventService.deleteEvent(e, user);
            }
        }

        Collection<Business> businesses = businessService.getUserBusinesses(user.getStringUUID());
        for(Business b : businesses){
            businessService.leaveBusiness(b.getUUID().toString(), user.getStringUUID());
        }

	    if(dao.delete(user)){
	        return "User successfully deleted.";
        }else{
	        return "Error deleting event";
        }
    }

    @Override
    public Collection<User> getUsers() {
        return dao.getAll().get();
    }

    @Override
    public UserSanitised getSantitised(User user) {  
        return new UserSanitised(user.getfName(), user.getlName(), user.getEmail());
    }

    //-----------------------------------------------------------------
    public User createGuest(String email, Event eventInvitedTo){
	    // Generate UUID
        UUID uuid = createGuestUserUUID(email, eventInvitedTo);

        // Create User object
        User guest = new User(uuid, "Unknown", "Unknown", email, "Unknown", "Unknown", false);
        guest.setIsGuest(true);

        // Store in DB - User table
        if(dao.save(guest)) {
            // Store in DB - IsGuest table
            this.newIsGuestRecord(guest, eventInvitedTo);
            return guest;
        }
        return null;
    }

    public Boolean newIsGuestRecord(User user, Event event){
        IsGuest isGuest = new IsGuest(user.getUUID(), event.getUUID());
        return guestDAO.save(isGuest);
    }
    
    public IsGuest getGuestEvent(User user, Event event){
        Optional<IsGuest> response = guestDAO.get(user, event);
        if(response.isPresent()){
            return response.get();
        }
        return null;
    }

    public List<IsGuest> getGuestRecords(User user){
        Optional<List<IsGuest>> response = guestDAO.get(user);
        if(response.isPresent()){
            return response.get();
        }
        return null;
    }
    //-----------------------------------------------------------------

    // Returns a UUID generated from user specific seed data
    public static UUID createUserUUID(String fName, String lName, String email){
        String uuidData = fName + lName + email;
        UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
        return uuid;
    }

    public static UUID createGuestUserUUID(String email, Event event){
        long time = Instant.now().getEpochSecond();
        String strTime = Long.toString(time);
	    String uuidData = "Guest" + email + event.getName() + strTime;
	    UUID uuid = UUID.nameUUIDFromBytes(uuidData.getBytes());
	    return uuid;
    }

    // Returns a random salt string
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

    // Takes plain text password string and returns hashed version 
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

    //TODO comment
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

    //TODO comment
    public static byte[] fromHex(String hex){
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    // Returns true if provided email does not belong to any user
    public boolean checkUniqueEmail(String email){
        if (dao.get(email).isPresent()){
            return false;
        }
        else{return true;}
    }

    //TODO these methods should belong to the Token service
    public String getUserToken(User user){
        // User needs new token issued
        // Check if user currently has a token issued
        // If they do, remove it and issue a new one

        /*
        if(dao.CheckUserToken(user.getStringUUID())){
            // Remove tokens
            dao.removeAllUserToken(user.getStringUUID());
        }
        */

        Collection<Token> tokens = tokenDao.getAll(user).get();
        for (Token t : tokens){
            tokenDao.delete(t);
        }

        // Create new token
        String token = createAPItoken(user.getUUID().toString(), user.getfName(), user.getlName(),
                user.getEmail(), user.getSalt());

        // Add to DB
        long tokenExpires = Instant.now().getEpochSecond() + 3600;  // Token expires an hour from when it was created
        //String feedback = dao.createToken(user.getUUID().toString(), token, tokenExpires);
        if(Boolean.TRUE.equals(tokenDao.save(new Token(token, user.getUUID(), tokenExpires)))) {
            return token;
        }else{
            return "Failed to create token";
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
        Optional<User> response = dao.get(UUID.fromString(uuid));
	    if(response.isPresent()){
	        return response.get();
        }
	    return null;
    }

    public User getUserByEmail(String email){
        Optional<User> response = dao.get(email);
	    if(response.isPresent()){
	        return response.get();
        }
	    return null;
    }

    // Function checks if the credentials that are input when creating an account are valid (long password, etc)
    public Boolean isValidRegister(String firstName, String lastName, String email, String password) {

	    // All fields are required
	    if (firstName.length() == 0 || lastName.length() == 0 || email.length() == 0 || password.length() == 0) {

	        return false;

        }

	    // Email should contain an @ and .
	    if (!email.contains("@") && !email.contains(".")) {

	        return false;

        }

	    // Check email is not currently in use
        User u = this.getUserByEmail(email);
	    if(!(u == null) && !u.getIsGuest()){
	        return false;
        }

	    if (password.length() <= 5 ) {

	        return false;

        }

	    return true;

    }

    // Function checks if the credentials that are input when updating an account are valid
    public Boolean isValidUpdate(String firstName, String lastName, String email) {

	    // All fields are required
	    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {

	        return false;

        }

	    // Email should contain an @ and .
	    if (!email.contains("@") && !email.contains(".")) {

	        return false;

        }

	    return true;

    }

    public Boolean passwordMeetsRequirements(String password){
	    // Must be longer than 5 characters
        if(password.length() < 6){
            return false;
        }
        return true;
    }

    public String updateUserPassword(User user, String currentPassword, String newPassword, String passwordConfirmation){
	    // Check current password is correct
        User loginCheck = validationService.validate(user.getEmail(), currentPassword);
        if(loginCheck != null){
            // Check new password and password confirmation match
            if(newPassword.equals(passwordConfirmation)){
                // Check new password meets requirements
                if(this.passwordMeetsRequirements(newPassword)) {
                    // Get new password hash
                    byte[] salt = fromHex(user.getSalt());
                    byte[] hash = generateHash(newPassword, salt);

                    // Update DB
                    if(dao.updatePassword(user, this.toHex(hash))){
                        return "Password successfully updated";
                    }
                    return "Error updating password";
                }
                return "New password is invalid";
            }
            return "New password and password confirmation do not match";
        }
        return "Current Password is not correct";
    }

    // Returns true if User is an admin, false otherwise
    // By default when a new user is created isAdmin = 0. Set isAdmin to 1 through phpMyAdmin to make a User an admin.
    public Boolean isAdmin(User user) {

        return dao.isAdmin(user) == 1;

    }

}
