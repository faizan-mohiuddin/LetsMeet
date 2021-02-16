package com.LetsMeet.LetsMeet.User.Service;

import java.util.Collection;

import com.LetsMeet.LetsMeet.User.Model.UserSanitised;
import com.LetsMeet.LetsMeet.User.Model.User;

public interface UserServiceInterface {

    // Creation/Deletion
    public abstract String createUser(String fName, String lName, String email, String password);
    public abstract void updateUser(String uuid, User user);
    public abstract String deleteUser(User user);
    public abstract Collection<User> getUsers();

    // Returns a sanitized (stripped of backend data) version of the User object
    public abstract UserSanitised getSantitised(User user);

    // Managment
    //TODO add other method definitions here
    public abstract Boolean isValidRegister(String fName, String lName, String email, String password);
    public abstract Boolean updateUser2(String useruuid, String fName, String lName, String email, String password);
}   
