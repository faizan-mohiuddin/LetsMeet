package com.LetsMeet.LetsMeet.Root.Media;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import com.LetsMeet.LetsMeet.Utilities.LetsMeetConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class MediaService {

    private static final Logger LOGGER=LoggerFactory.getLogger(MediaService.class);
    
    @Autowired
    MediaDAO mediaDao;

    @Autowired
    LetsMeetConfiguration config;

    public Optional<Media> newMedia(MultipartFile file, String... dirArgs){
        try{
            // Create new Media model object
            var media = newMedia(file.getOriginalFilename(), file.getContentType(), dirArgs).orElseThrow();
            // Save media object with filestream of upload
            mediaDao.save(media,file.getInputStream());
            
            LOGGER.debug("Multipart file uploaded as {} to {}",media.getPath().getFileName(),media.getPath());

            return Optional.of(media);
        }
        catch(Exception e){
            return Optional.empty();
        }
    }

    public Optional<Media> newMedia(String name, String type, String... dirArgs){
        // Set the media object path
        Path path = mediaDirectory(dirArgs).resolve(UUID.randomUUID().toString() + "_" + name);
        Media media = new Media(UUID.randomUUID(),path, type);

        LOGGER.debug("Media file created at {}", path);

        // Return new media object created at this path
        return Optional.of(media);
    }

    public String generateURL(Media media){
        String[] subDirs = media.getPath().toString().split(Pattern.quote(FileSystems.getDefault().getSeparator()));
        return "media/" + String.join("/", subDirs);
    }

    private Path mediaDirectory(String... dirArgs){
        if (dirArgs.length == 0) { return Paths.get("files", "misc");}
        else return Paths.get("files", dirArgs);
    }
}
