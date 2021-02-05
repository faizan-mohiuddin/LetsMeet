package com.LetsMeet.LetsMeet.Utilities;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnector {

    @Autowired
    private LetsMeetConfiguration config = new LetsMeetConfiguration();

    public Connection con;

    public DBConnector(){
        try {
            this.Connect();
        }catch(Exception e){
            System.out.println("\nDBConnector: Initilise");
            System.out.println(e);
            this.conRetry();
            e.printStackTrace();
        }
    }

    public void open(){
        try {
            this.Connect();
        }catch(Exception e){
            System.out.println("\nDBConnector: open");
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
        System.out.println("\nConnecting to DB");
        System.out.println(this.config.getDatabaseHost());

        this.con = DriverManager.getConnection(this.config.getDatabaseHost() + "/" + this.config.getDatabaseName(),
                this.config.getDatabaseUser(), this.config.getDatabasePassword());
    }

    public void close(){
        try {
            this.con.close();
        }catch(Exception e){
            System.out.println("\nDBConnector: closeCon");
            System.out.println(e);
            if(this.con != null) {
                this.close();
            }
        }
    }

    public boolean checkCon(){
        if(con == null){
            return false;
        }
        return true;
    }
}