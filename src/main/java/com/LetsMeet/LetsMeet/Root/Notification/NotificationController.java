package com.LetsMeet.LetsMeet.Root.Notification;

import javax.annotation.PostConstruct;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    
    private static final Logger LOGGER=LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    NotificationService notify;

    @Autowired
    ValidationService security;

    @PostMapping("api/root/notification")
    public ResponseEntity<Object> postNotification(@RequestParam(value="Token", defaultValue = "none") String token){
        try{
            User user = security.getAuthenticatedUser(token);
            notify.send(Notifications.simpleMail("testing", "helloworld", "google.com"), user);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}
