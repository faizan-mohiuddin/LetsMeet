package com.LetsMeet.LetsMeet.Root.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import com.LetsMeet.LetsMeet.Root.Media.Media;
import com.LetsMeet.LetsMeet.Root.Media.MediaService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;

@RestController
public class MediaManagerAPI {

    private static final Logger LOGGER=LoggerFactory.getLogger(MediaManagerAPI.class);


    @Autowired
    MediaService mediaService;

    @Autowired
    ValidationService validationService;

    @PostMapping("api/media")
    public ResponseEntity<String> uploadFile(
        @RequestParam(value="Token", defaultValue = "none") String token, 
        @RequestParam(value = "file") MultipartFile file){

        User user = validationService.getAuthenticatedUser(token);

        if (user == null){return new ResponseEntity<>(HttpStatus.FORBIDDEN);}

        try{
            String fileURL = mediaService.saveMedia(new Media(file,user.getUUID())).orElseThrow(IOException::new);
            return new ResponseEntity<>(fileURL, HttpStatus.OK);
        }
        catch (Exception e){
            LOGGER.error("Could not save file {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
