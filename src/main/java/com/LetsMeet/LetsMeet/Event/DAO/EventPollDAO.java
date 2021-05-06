package com.LetsMeet.LetsMeet.Event.DAO;

import java.util.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.LetsMeet.LetsMeet.Event.Poll.PollDAO;
import com.LetsMeet.LetsMeet.Event.Poll.Model.*;
import com.LetsMeet.LetsMeet.Root.Core.Model.LetsMeetTuple;
import com.LetsMeet.LetsMeet.Root.Database.ConnectionService;
import com.LetsMeet.LetsMeet.Root.Database.Model.DatabaseConnector;
import com.LetsMeet.LetsMeet.Utilities.DAOconjugate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventPollDAO implements DAOconjugate<LetsMeetTuple<UUID,Poll>> {

        // Get logger
        private static final Logger LOGGER=LoggerFactory.getLogger(EventPollDAO.class);

        @Autowired
        ConnectionService connectionService;

        @Autowired
        private PollDAO pollDAO;

        @Override
        public Optional<LetsMeetTuple<UUID, Poll>> get(UUID eventUUID, UUID pollUUID) throws IOException {
            try(DatabaseConnector connector = connectionService.get();
                Statement statement = connector.getConnection().createStatement();){

                String query = String.format("select * from EventHasPoll where EventHasPoll.EventUUID = '%s' and EventHasPoll.PollUUID = '%s'", eventUUID.toString(), pollUUID.toString());

                ResultSet rs = statement.executeQuery(query);
                rs.next();

                UUID event = UUID.fromString(rs.getString("EventUUID"));
                Poll poll = pollDAO.get(UUID.fromString(rs.getString("PollUUID"))).orElseThrow();

                return Optional.ofNullable(new LetsMeetTuple<>(event, poll));
            }
            catch(Exception e){
                LOGGER.error("Failed to get Event Poll: {} ", e.getMessage());
                return Optional.empty();
            }
        }

        /**
         * Returns all Polls related to the given event 
         * @param uuid of Event
         * @return List of polls, may be empty if none present
         * @throws IOException
         */
        public Optional<List<Poll>> get(UUID uuid) throws IOException {
                try(DatabaseConnector connector = connectionService.get();
                Statement statement = connector.getConnection().createStatement();){
                String query = String.format("select * from EventHasPoll where EventHasPoll.EventUUID = '%s'", uuid);

                ResultSet rs = statement.executeQuery(query);
                List<Poll> records = new ArrayList<>();

                while(rs.next()){
                    records.add(pollDAO.get(UUID.fromString(rs.getString("PollUUID"))).orElseThrow());
                }
                return Optional.ofNullable(records);

            }
            catch(Exception e){
                LOGGER.error("Failed to get EventPermission: {} ", e.getMessage());
                return Optional.empty();
            }
        }

        @Override
        public Optional<Collection<LetsMeetTuple<UUID, Poll>>> getAll() throws IOException {
            LOGGER.warn("EventPoll: getAll() not supported. Request Polls explicitly");
            return Optional.empty();
        }

        @Override
        public Boolean save(LetsMeetTuple<UUID, Poll> t) throws IOException {
            try(DatabaseConnector connector = connectionService.get();
            PreparedStatement statement = connector.getConnection().prepareStatement("INSERT INTO EventHasPoll (EventUUID, PollUUID) VALUES (?,?)")){

                statement.setString(1, t.x.toString());
                statement.setString(2, t.y.getUUID().toString());
    
                return (statement.executeUpdate() > 0);
            }
            catch(Exception e){
                LOGGER.error("Unable to save: {}", e.getMessage());
                return false;
            }
        }

        @Override
        public Boolean update(LetsMeetTuple<UUID, Poll> t) throws IOException {
            LOGGER.warn("EventPoll: update() not supported.");
            return false;
        }

        @Override
        public Boolean delete(LetsMeetTuple<UUID, Poll> t) throws IOException {
            return delete(t.x.toString(),t.y.getUUID().toString());
        }

        @Override
        public Boolean delete(String eventUUID, String pollUUID) throws IOException {
            try(DatabaseConnector connector = connectionService.get();
            Statement statement = connector.getConnection().createStatement();){

            String query = String.format("DELETE FROM EventHasPoll WHERE EventHasPoll.EventUUID = '%s' AND EventHasPoll.PollUUID = '%s'", eventUUID, pollUUID);

            return (statement.executeUpdate(query) > 0);

        }catch(Exception e){
            LOGGER.error("Failed to remove Poll from Event: {} ", e.getMessage());
            return false;
        }
    }

        


    
}
