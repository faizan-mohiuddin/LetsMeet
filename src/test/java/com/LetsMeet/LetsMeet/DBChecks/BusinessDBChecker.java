package com.LetsMeet.LetsMeet.DBChecks;


import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.Statement;

@Component
public class BusinessDBChecker {
    @Autowired
    LetsMeetConfiguration config;

    @Autowired
    DBConnector database;

    public String businessUUIDFromName(String name){
        database.open();
        try{
            Statement statement = database.con.createStatement();
            String query = String.format("SELECT Business.BusinessUUID FROM Business WHERE Business.Name = '%s'", name);
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String response = rs.getString(1);
            database.close();
            return response;
        }catch(Exception e){
            database.close();
            System.out.println("\nBusinessDBChecker: businessUUIDFromName");
            System.out.println(e);
            return null;
        }
    }
}
