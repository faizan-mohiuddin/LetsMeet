package com.LetsMeet.LetsMeet.Root.Media;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MediaService {

    private static final Logger LOGGER=LoggerFactory.getLogger(MediaService.class);
    
    @Autowired
    MediaDAO mediaDao;

    public Optional<String> saveMedia(Media t){
        try{
            mediaDao.save(t);
        }
        catch (Exception e){
            LOGGER.error("Unable to save: {}", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(t.getURL());
        
    }
}
