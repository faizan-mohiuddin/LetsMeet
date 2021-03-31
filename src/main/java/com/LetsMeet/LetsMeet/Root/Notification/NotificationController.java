package com.LetsMeet.LetsMeet.Root.Notification;

import com.LetsMeet.LetsMeet.Root.Notification.Model.Notification;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

            StringBuffer sb = new StringBuffer();

            StringBuffer buffer = sb.append("BEGIN:VCALENDAR\n" +
                    "PRODID:-//Microsoft Corporation//Outlook 9.0 MIMEDIR//EN\n" +
                    "VERSION:2.0\n" +
                    "METHOD:REQUEST\n" +
                    "BEGIN:VEVENT\n" +
                    "ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:admin@admin.com\n" +
                    "ORGANIZER:MAILTO:xx@xx.com\n" +
                    "DTSTART:20051208T053000Z\n" +
                    "DTEND:20051208T060000Z\n" +
                    "LOCATION:Conference room\n" +
                    "TRANSP:OPAQUE\n" +
                    "SEQUENCE:0\n" +
                    "UID:040000008200E00074C5B7101A82E00800000000002FF466CE3AC5010000000000000000100\n" +
                    " 000004377FE5C37984842BF9440448399EB02\n" +
                    "DTSTAMP:20051206T120102Z\n" +
                    "CATEGORIES:Meeting\n" +
                    "DESCRIPTION:This the description of the meeting.\n\n" +
                    "SUMMARY:Test meeting request\n" +
                    "PRIORITY:5\n" +
                    "CLASS:PUBLIC\n" +
                    "BEGIN:VALARM\n" +
                    "TRIGGER:PT1440M\n" +
                    "ACTION:DISPLAY\n" +
                    "DESCRIPTION:Reminder\n" +
                    "END:VALARM\n" +
                    "END:VEVENT\n" +
                    "END:VCALENDAR");

            //Notification notification = Notifications.simpleMail("testing", "helloworld", "google.com");
            Notification notification = Notifications.withFile("testing", "body", "test.ics", String.valueOf(buffer).getBytes(), true, "action");
            notify.send(notification, user);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

}
