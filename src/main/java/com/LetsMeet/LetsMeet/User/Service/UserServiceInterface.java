package com.LetsMeet.LetsMeet.User.Service;

import java.util.Collection;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Model.User_Internal;

public interface UserServiceInterface {
    public abstract String createUser(String fName, String lName, String email, String password);
    public abstract void updateUser(String uuid, User_Internal user);
    public abstract String deleteUser(User_Internal user);
    public abstract Collection<User> getUsers(); 
}
