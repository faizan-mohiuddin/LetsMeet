package com.LetsMeet.LetsMeet.User.Service;

import java.util.Collection;

import com.LetsMeet.LetsMeet.User.DAO.UserDao;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    UserDao dao;


	@Override
	public void createUser(User user) {
        // Do validation and security business
        dao.save(user);
		
	}

    @Override
    public void updateUser(String uuid, User user) {
        dao.update(user);

    }

    @Override
    public void deleteUser(String uuid) {
        dao.delete(uuid);

    }

    @Override
    public Collection<User> getUsers() {
        System.out.println("hey from userservice.java!");
        return dao.getAll();
    }
    
}
