package com.LetsMeet.LetsMeet.Root.Media.VenueImages;

import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class VenueImageService {
    private static final Logger LOGGER= LoggerFactory.getLogger(VenueImageService.class);

    @Autowired
    LetsMeetConfiguration config;

    public void save(VenueImage i) throws IOException {
        // Check for common image errors
        if (i.image == null){throw new IllegalArgumentException("No media file");}
        if (i.image.getOriginalFilename().length() < 1){throw new IllegalArgumentException("invalid name");}

        // Save file within file system
        Path uploadPath = Paths.get(config.getdataFolder() +"\\Venues\\" + i.venueUUID);
        LOGGER.debug("Saving {} to path: {}",i.image.getOriginalFilename(), uploadPath);

        // If required folder is not already created, create it
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = i.image.getInputStream()) {
            Path filePath = uploadPath.resolve(i.getSaveName());
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ioe) {
            throw new IOException("Could not save file: " + i.image.getOriginalFilename(), ioe);
        }

    }
}
