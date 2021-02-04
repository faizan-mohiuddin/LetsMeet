//-----------------------------------------------------------------
// LetsMeetConfiguration.java
// Let's Meet 2021
//
// Responsible for loading and storing confirguration variables

package com.LetsMeet.LetsMeet.Utilities;

//-----------------------------------------------------------------

import org.springframework.boot.context.properties.*;
import org.springframework.stereotype.*;

//-----------------------------------------------------------------

@ConfigurationProperties(prefix="lm.config")
@Component
public class LetsMeetConfiguration {
    
    private static String databaseHost;
    private static String databaseName;
    private static String databaseUser;
    private static String databasePassword;

    //-----------------------------------------------------------------

    public void setDatabaseHost(String x){ databaseHost = x;}
    public String getDatabaseHost(){return databaseHost;}

    public void setDatabaseName(String x){ databaseName = x;}
    public String getDatabaseName(){return databaseName;}

    public void setDatabaseUser(String x){ databaseUser = x;}
    public String getDatabaseUser(){return databaseUser;}

    public void setDatabasePassword(String x){ databasePassword = x;}
    public String getDatabasePassword(){return databasePassword;}






}
