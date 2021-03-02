package com.LetsMeet.LetsMeet.Root.Media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.LetsMeet.LetsMeet.Utilities.DAO;
import com.LetsMeet.LetsMeet.Utilities.DatabaseInterface;

import org.springframework.stereotype.Component;

@Component
public class MediaDAO implements DAO<Media> {

    @Override
    public Optional<Media> get(UUID uuid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Collection<Media>> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean save(Media t) throws IOException {

        // Save file within file system
        Path uploadPath = Paths.get(t.getPath());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = t.getFile().getInputStream()) {
            Path filePath = uploadPath.resolve(t.getFilename());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } 
        catch (IOException ioe) {        
            throw new IOException("Could not save file: " + t.getFile().getOriginalFilename(), ioe);
        }

        // Reference file within database
        try(PreparedStatement statement = DatabaseInterface.get().prepareStatement("INSERT INTO Media (MediaUUID, UserUUID, Type, Path) VALUES (?,?,?,?)")){

            statement.setString(1, t.getUuid().toString());
            statement.setString(2, t.getOwner().toString());
            statement.setString(3, t.getFile().getContentType());
            statement.setString(4, t.getURL());

            if(statement.executeUpdate() > 0){
                DatabaseInterface.drop();
                return true;
            }else{
                throw new Exception("Nothing added to DB");
            }

        }catch(Exception e){
            System.out.println("Event Dao : save");
            DatabaseInterface.drop();
            e.printStackTrace();
            return false;
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
