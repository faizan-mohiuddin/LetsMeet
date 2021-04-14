package com.LetsMeet.LetsMeet.User.DAO;

import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;
import com.LetsMeet.LetsMeet.User.Model.IsGuest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GuestDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuestDAO.class);

    @Autowired
    LetsMeetConfiguration config;

    @Autowired
    ConnectionService connectionService;

    public Boolean save(IsGuest g){
        DatabaseConnector connector = connectionService.get();
        try{
            PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO IsGuest (GuestUUID, EventUUID) VALUES (?,?)");
            statement.setString(1, g.getGuestUUID().toString());
            statement.setString(2, g.getEventUUID().toString());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                return true;
            } else {
                throw new SQLException("Nothing added to DB");
            }
        }catch (Exception e){
            LOGGER.warn("Error saving IsGuest Data: {}", e.getMessage());
            return false;
        }
    }

    public Optional<IsGuest> get(User user, Event event){
        try {
            DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();
            String query = String.format("SELECT * FROM IsGuest AS g WHERE g.GuestUUID = '%s' AND g.EventUUID = '%s'",
                    user.getUUID().toString(), event.getUUID().toString());
            ResultSet rs = statement.executeQuery(query);

            rs.next();
            Optional<IsGuest> record = Optional.of(new IsGuest(rs.getString(1), rs.getString(2)));
            return record;
        }catch(Exception e){
            LOGGER.warn(e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<List<IsGuest>> get(User user){
        try{
            DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();
            String query = String.format("SELECT * FROM IsGuest AS g WHERE g.GuestUUID = '%s'", user.getUUID().toString());
            ResultSet rs = statement.executeQuery(query);

            List<IsGuest> records = new ArrayList<>();
            while(rs.next()){
                records.add(new IsGuest(rs.getString(1), rs.getString(2)));
            }
            return Optional.of(records);

        }catch(Exception e){
            LOGGER.warn(e.getMessage());
            return Optional.empty();
        }
    }

    public Boolean removeUser(User user){
        try{
            DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();
            String query = String.format("DELETE FROM IsGuest WHERE IsGuest.GuestUUID = '%s'", user.getUUID().toString());
            statement.executeUpdate(query);

            return true;
        }catch (Exception e){
            LOGGER.warn("Error removing guest user instances: {}", e.getMessage());
            return false;
        }
    }
}
