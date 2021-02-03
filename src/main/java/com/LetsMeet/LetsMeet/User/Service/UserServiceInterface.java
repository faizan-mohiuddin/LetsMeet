package com.LetsMeet.LetsMeet.User.Service;

import java.util.Collection;

import com.LetsMeet.LetsMeet.User.Model.User;

public interface UserServiceInterface {
    public abstract void createUser(User user);
    public abstract void updateUser(String uuid, User user);
    public abstract void deleteUser(String uuid);
    public abstract Collection<User> getUsers(); 
}
