package com.LetsMeet.Models.Connectors;

import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {
    private LetsMeetConfiguration config = new LetsMeetConfiguration();
    public Connection con;

    public DBConnector(){
        try {
            this.Connect();
        }catch(Exception e){
            System.out.println("\nDBConnector: Initilise");
            System.out.println(e);
            this.conRetry();
        }
    }

    private void conRetry(){
        try {
            this.Connect();
        }catch(Exception e){
            System.out.println("\nDBConnector: conRetry");
            System.out.println(e);
        }
    }

    private void Connect() throws Exception{
        this.con = DriverManager.getConnection(this.config.getDatabaseHost(), this.config.getDatabaseUser(),
                this.config.getDatabasePassword());
    }

    public void closeCon(){
        try {
            this.con.close();
        }catch(Exception e){
            System.out.println("\nDBConnector: closeCon");
            System.out.println(e);
            if(this.con != null) {
                this.closeCon();
            }
        }
    }
}
