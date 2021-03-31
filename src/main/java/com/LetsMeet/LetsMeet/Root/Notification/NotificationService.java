package com.LetsMeet.LetsMeet.Root.Notification;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.LetsMeet.LetsMeet.Root.Notification.Model.Notification;
import com.LetsMeet.LetsMeet.User.Model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class NotificationService {
    
    @Autowired
    JavaMailSender emailSender;

    @Autowired
    SpringTemplateEngine templateEngine;

    /**
     * Sends a notification to the given users. The quickest way to create a Notification is with: {@link com.LetsMeet.LetsMeet.Root.Notification.Notifications}
     * @param notification Notification object. Create using 
     * @param users Each user the notification shall be sent to
     * @return True if all notifications sent correctly, false otherwise.
     */
    @Async
    public Future<Boolean> send(Notification notification, User... users){
        try{
            //save to db

            // send notifications
            switch (notification.getType()) {
                case MAIL_ONLY: email(notification, users); break;
                case ALERT_ONLY: alert(); break;
                case ALERT_AND_MAIL: alert(); email(notification, users); break;
                default: alert(); break;
            }
            return new AsyncResult<>(true);
        }
        catch(Exception e){
            e.printStackTrace();
            return new AsyncResult<>(false);
        }
    }

    private Boolean email(Notification notification, User... users) throws MessagingException{

        for (User user : users){
            MimeMessage mail = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            // Process email template
            Context context = new Context();
            context.setVariables(notification.getModel());
            String html = templateEngine.process(notification.getTemplate(), context);
            helper.setText(html, true);

            // Process attachments
            for (Notification.File f : notification.getFiles()){
                helper.addAttachment(f.filename, f.data);
            }
            
            // Set values  
            helper.setTo(user.getEmail());
            helper.setSubject(notification.getTitle());
            helper.setFrom("no-reply@letsmeet.com");

            emailSender.send(mail);
        }
        
        return true;
    }

    private Boolean alert(){
        return true;
    }
}
