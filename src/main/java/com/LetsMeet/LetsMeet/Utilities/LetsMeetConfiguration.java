//-----------------------------------------------------------------
// LetsMeetConfiguration.java
// Let's Meet 2021
//
// Responsible for loading and storing configuration variables

package com.LetsMeet.LetsMeet.Utilities;

//-----------------------------------------------------------------

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.InetAddress;

//-----------------------------------------------------------------

@ConfigurationProperties(prefix="lm.config")
@Component
public class LetsMeetConfiguration {

    @Autowired
    Environment environment;

    private String databaseHost;
    private String databaseName;
    private String databaseUser;
    private String databasePassword;
    private int connectionLimit;
    private int connectionTarget;

    private String dataFolder;

    private String GmapsKey;

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

    public void setconnectionTarget(int x){ connectionTarget = x;}
    public int getconnectionTarget(){return connectionTarget;}
    

    public void setdataFolder(String x){ dataFolder = x;}
    public String getdataFolder(){return dataFolder;}

    public void setGmapsKey(String x){GmapsKey = x;}
    public String getGmapsKey(){return GmapsKey;}

    public String getApplicationHost(){
        try {
            String port = environment.getProperty("local.server.port");
            String address;
            if(port != null) {
                address = InetAddress.getLoopbackAddress().getHostName() + ":" + port;
            }else{
                address = InetAddress.getLoopbackAddress().getHostName();
            }
            return address;
        }catch (Exception e){

        }
        return null;
    }
}
