package com.LetsMeet.LetsMeet.Root.Notification.Model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.util.ByteArrayDataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
/**
 * Models a notification
 * @author Hamish Weir
 */
@EntityScan
public class Notification {

    public enum Type{
        ALERT_AND_MAIL,
        ALERT_ONLY,
        MAIL_ONLY
    }

    public static class File{
        public String filename;
        public ByteArrayDataSource data;

        public File(String filename, ByteArrayDataSource data){this.filename = filename; this.data = data;}
    }
    
    private UUID uuid;
    private String title;
    private Map<String,Object> model;
    private List<File> files;
    private String template;
    private Type type;
    private ZonedDateTime created;


    public Notification(UUID uuid, String title, Map<String,Object> model, List<File> files, String template, Type type, ZonedDateTime created) {
        this.uuid = uuid;
        this.title = title;
        this.model = model;
        this.files = files;
        this.template = template;
        this.type = type;
        this.created = created;
    }

    public Notification(String title, Map<String,Object> model, String template, Type type){
        this(UUID.randomUUID(), title, model,new ArrayList<>(), template, type, ZonedDateTime.now());
    }


    public UUID getUuid() {
        return uuid;
    }


    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public Map<String, Object> getModel() {
        return model;
    }


    public void setModel(Map<String, Object> model) {
        this.model = model;
    }


    public String getTemplate() {
        return template;
    }


    public void setTemplate(String template) {
        this.template = template;
    }


    public Type getType() {
        return type;
    }


    public void setType(Type type) {
        this.type = type;
    }


    public ZonedDateTime getCreated() {
        return created;
    }


    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    
}
