package com.LetsMeet.LetsMeet.Business.DAO;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DBConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Component
public class BusinessDAO implements DAO<Business> {

    @Autowired
    DBConnector database;

    @Override
    public Optional<Business> get(UUID businessUUID) {
        database.open();
        try(Statement statement = database.getCon().createStatement()){
            String query = String.format("select * from Business where Business.BusinessUUID = '%s'", businessUUID.toString());

            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Optional<Business> response = Optional.of(new Business(UUID.fromString(rs.getString(1)),
                    rs.getString(2)));
            database.close();
            return response;

        }catch(Exception e){
            database.close();
            System.out.println("\nBusiness Dao: get (UUID)");
            System.out.println(e);
            return Optional.empty();

        }
    }

    @Override
    public Optional<Collection<Business>> getAll() {
        return Optional.empty();
    }

    @Override
    public Boolean save(Business business) {
        database.open();

        // Save the event
        try(PreparedStatement statement = database.getCon().prepareStatement("INSERT INTO Business (BusinessUUID, Name) VALUES (?,?)")){
            statement.setString(1, business.getUUID().toString());
            statement.setString(2, business.getName());


            if(statement.executeUpdate() > 0){
                database.close();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("Business Dao : save");
            database.close();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean update(Business business) {
        return null;
    }

    @Override
    public Boolean delete(Business business) {
        return null;
    }

    @Override
    public Boolean delete(UUID uuid) {
        database.open();
        try(Statement statement = database.con.createStatement()){

            String query;
            String businessUUID = uuid.toString();

            query = String.format("DELETE FROM Business where Business.BusinessUUID = '%s'", businessUUID);
            statement.executeUpdate(query);

            return true;

        }catch(Exception e){
            System.out.println("Business Dao: delete (UUID)");
            database.close();
            e.printStackTrace();
            return false;
        }
    }
}
