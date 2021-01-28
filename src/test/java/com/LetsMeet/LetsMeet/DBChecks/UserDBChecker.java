package com.LetsMeet.LetsMeet.DBChecks;

import java.sql.*;

public class UserDBChecker {

    Connection con;

    public UserDBChecker(){
        try{
            this.con = DriverManager.getConnection("jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2383522",
                    "sql2383522", "iN8!qL4*");
        }catch(Exception e){
            System.out.println("\nUser Model: initilise");
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
}
