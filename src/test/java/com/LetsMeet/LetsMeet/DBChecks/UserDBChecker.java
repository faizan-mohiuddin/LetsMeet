package com.LetsMeet.LetsMeet.DBChecks;

import java.sql.*;

public class UserDBChecker {

    Connection con;

    public UserDBChecker(){
        try{
            this.con = DriverManager.getConnection("jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2383522",
                    "sql2383522", "iN8!qL4*");
        }catch(Exception e){
            System.out.println("\nUser Checker: initilise");
            System.out.println(e);
        }
    }

    public void closeCon(){
        try {
            this.con.close();
        }catch(Exception e){
            System.out.println("\nUser Model: closeCon");
            System.out.println(e);
            if(this.con != null) {
                this.closeCon();
            }
        }
    }

    public void removeUserByEmail(String email){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("DELETE FROM User WHERE User.email = '%s'", email);
            statement.executeUpdate(query);
        }catch(Exception e){
            System.out.println("UserDBChecker : removeUserByEmail");
            System.out.println(e);
        }
    }

    public boolean checkForToken(String token, String UserUUID){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("SELECT Token.UserUUID, Token.TokenUUID FROM Token " +
                    "WHERE Token.TokenUUID = '%s'", token);

            ResultSet rs = statement.executeQuery(query);
            rs.next();
            if(rs.getString("UserUUID").equals(UserUUID) && rs.getString("TokenUUID").equals(token)) {
                return true;
            }
            return false;
        }catch(Exception e){
            System.out.println("UserDBChecker : removeUserByEmail");
            System.out.println(e);
            return false;
        }
    }

    public String UserUUIDFromEmail(String email){
        try{
            Statement statement = this.con.createStatement();
            String query = String.format("SELECT User.UserUUID FROM User WHERE User.email = '%s'", email);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            return rs.getString("UserUUID");
        }catch(Exception e){
            System.out.println("UserDBChecker : UserUUIDFromEmail");
            System.out.println(e);
            return null;
        }
    }
}
