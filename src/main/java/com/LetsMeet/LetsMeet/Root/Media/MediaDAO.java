package com.LetsMeet.LetsMeet.Root.Media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DatabaseInterface;
import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MediaDAO implements DAO<Media> {

    private static final Logger LOGGER=LoggerFactory.getLogger(MediaDAO.class);

    @Autowired
    LetsMeetConfiguration config;

    @Override
    public Optional<Media> get(UUID uuid) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public Optional<Collection<Media>> getAll() {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    public Boolean save(Media t, InputStream data) throws IOException {
        // Save database entry
        save(t);

        // Save local file
        Path path = Paths.get(config.getdataFolder()).resolve(t.getPath());

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        try {
            Files.copy(data, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }


    @Override
    public Boolean save(Media t) throws IOException {

        // Reference file within database
        try(PreparedStatement statement = DatabaseInterface.get().prepareStatement("INSERT INTO Media (MediaUUID, UserUUID, Type, Path) VALUES (?,?,?,?)")){

            statement.setString(1, t.getUuid().toString());
            statement.setString(4, t.getPath().toString());
            statement.setString(3, t.getType());
            statement.setString(2, "not required");

            if(statement.executeUpdate() > 0){
                DatabaseInterface.drop();
                return true;
            }else{
                throw new IOException("Nothing added to DB");
            }

        }catch(Exception e){
            DatabaseInterface.drop();
            throw new IOException("Could not reference media file in database", e);
        }
    }

    @Override
    public Boolean update(Media t) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Boolean delete(Media t) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Boolean delete(UUID uuid) {
        // TODO Auto-generated method stub
        return false;
    }
    
}
