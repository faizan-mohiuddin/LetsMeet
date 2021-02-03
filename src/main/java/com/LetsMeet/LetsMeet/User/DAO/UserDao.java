package com.LetsMeet.LetsMeet.User.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDao implements DAO<User> {

    @Autowired
    LetsMeetConfiguration config;

    Connection con;

    private void open(){
        try{
        this.con = DriverManager.getConnection("jdbc:mysql://"+config.getDatabaseHost() + "/" + config.getDatabaseName(), config.getDatabaseUser(), config.getDatabasePassword());
        }
        catch(Exception e){
            System.out.println("UserDao Exception:\n" + e);
        }
    }

    private void close(){
        try{
            this.con.close();
        }
        catch (Exception e){
            System.out.println("UserDao Exception:\n" + e);
        }
    }



    @Override
    public Optional<User> get(String uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<User> getAll() {
        // TODO Auto-generated method stub
        System.out.println("hey from userDao.java!");
        System.out.println(config.getDatabaseHost());
        return null;
    }

    @Override
    public int save(User t) {
        // TODO Auto-generated method stub
        this.open();

        try(PreparedStatement statement = this.con.prepareStatement("INSERT INTO User (UserUUID, fName, lName, email, PasswordHash, salt) VALUES (?,?,?,?,?,?)")) {
            statement.setString(1, t.getUUID());
            statement.setString(2, t.getfName());
            statement.setString(3, t.getlName());
            statement.setString(4, t.getEmail());
            statement.setString(5, t.getPasswordHash());
            statement.setString(6, t.getSalt());
            int rows = statement.executeUpdate();
            
            this.close();

            if (rows > 0) {
                return 1;
            } else {
                throw new Exception("Nothing added to DB");
            }

        } catch (Exception e) {
            System.out.println("\nUser Model: newUser" + e);
            return 0;
        }
    }

    @Override
    public void update(User t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(User t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(String uuid) {
        // TODO Auto-generated method stub

    }
    
    
}
