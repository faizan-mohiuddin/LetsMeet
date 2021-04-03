//-----------------------------------------------------------------
// LetsMeetConfiguration.java
// Let's Meet 2021
//
// Responsible for loading and storing configuration variables

package com.LetsMeet.LetsMeet.Utilities;

//-----------------------------------------------------------------

import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;

//-----------------------------------------------------------------

@ConfigurationProperties(prefix="lm.config")
@Component
public class LetsMeetConfiguration {
    
    private String databaseHost;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private int connectionLimit;

    private String dataFolder;

    //-----------------------------------------------------------------

    public void setDatabaseHost(String x){ databaseHost = x;}
    public String getDatabaseHost(){return databaseHost;}

    public void setDatabaseName(String x){ databaseName = x;}
    public String getDatabaseName(){return databaseName;}

    public void setDatabaseUser(String x){ databaseUser = x;}
    public String getDatabaseUser(){return databaseUser;}

    public void setDatabasePassword(String x){ databasePassword = x;}
    public String getDatabasePassword(){return databasePassword;}

    public void setconnectionLimit(int x){ connectionLimit = x;}
    public int getconnectionLimit(){return connectionLimit;}
    

    public void setdataFolder(String x){ dataFolder = x;}
    public String getdataFolder(){return dataFolder;}







}
