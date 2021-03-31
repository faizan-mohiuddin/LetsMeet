package com.LetsMeet.LetsMeet.Root.Notification;

import java.util.HashMap;
import java.util.Map;

import javax.mail.util.ByteArrayDataSource;

import com.LetsMeet.LetsMeet.Root.Notification.Model.Notification;
/**
 * Helper utility to create common notification types
 * @author Hamish Weir (signal32)
 */
public class Notifications {

    private Notifications() {
        throw new IllegalStateException("Utility class");
      }
    
    /**
     * Creates an Notification that will only appear when viewed on website or requested through API
     * @param title Short title that will appear at head of alert
     * @param body Longer descriptive text that will appear inside alert body
     * @param action URL links
     * @return Notification object
     */
    public static Notification simpleAlert(String title, String body, String... action){
        return simple(title, body, Notification.Type.ALERT_ONLY, action);
    }

    /**
     * Creates an Notification that will be sent as an email only
     * @param title Short title that will appear at head of alert
     * @param body Longer descriptive text that will appear inside alert body
     * @param action URL links
     * @return Notification object
     */
    public static Notification simpleMail(String title, String body, String... action){
        return simple(title, body, Notification.Type.MAIL_ONLY, action);
    }

    /**
     * Creates an Notification that will be sent as email and alert
     * @param title Short title that will appear at head of alert
     * @param body Longer descriptive text that will appear inside alert body
     * @param action URL links
     * @return Notification object
     */
    public static Notification simpleAlertAndMail(String title, String body, String... action){
        return simple(title, body, Notification.Type.ALERT_AND_MAIL, action);
    }


    private static Notification simple(String title, String body,Notification.Type type, String... action){
        Map<String,Object> model = new HashMap<>();
        model.put("title", title);
        model.put("body", body);
        for (int i = 0; i<action.length;i++){
            if (action[i].length() > 0){model.put("action"+i, action[i]);}
        }
        
        return new Notification(title, model, "root/mail/default", type);
    }

    /**
     * Creates a notification with an attached file.
     * @param title Short title that will appear at head of alert
     * @param body Longer descriptive text that will appear inside alert body
     * @param filename Name of the file as it will appear on email attachment (and in other locations)
     * @param data The files data
     * @param mailOnly If true, this notification will only be sent by email
     * @param action URL links
     * @return Notification object
     */
    public static Notification withFile(String title, String body, String filename, byte[] data, boolean mailOnly, String... action){
        Map<String,Object> model = new HashMap<>();
        model.put("title", title);
        model.put("body", body);
        for (int i = 0; i<action.length;i++){
            if (action[i].length() > 0){model.put("action"+i, action[i]);}
        }
        Notification notification = new Notification(title, model, "root/mail/default", (mailOnly) ? Notification.Type.MAIL_ONLY : Notification.Type.ALERT_AND_MAIL);
        notification.getFiles().add(new Notification.File(filename, new ByteArrayDataSource(data, "text/calendar")));
        
        return notification;
    }
}
