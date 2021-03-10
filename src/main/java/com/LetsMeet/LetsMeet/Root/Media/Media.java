package com.LetsMeet.LetsMeet.Root.Media;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class Media {
    private MultipartFile file;
    private String path;
    private UUID uuid;
    private UUID owner;

    public Media(MultipartFile file, String path, UUID uuid, UUID owner) {
        this.file = file;
        this.path = path;
        this.uuid = uuid;
        this.owner = owner;
    }


    public Media(MultipartFile file, String path, UUID owner){
        this(file, path, UUID.randomUUID(), owner);
    }

    public Media(MultipartFile file , UUID owner){
        this(file, "/misc",UUID.randomUUID(),owner);
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getURL(){
        return "/media/"+ this.path +"/" + this.getFilename();
    }

    public String getFilename(){
        return this.uuid.toString() + "_" + this.file.getOriginalFilename();
    }

    public UUID getOwner(){
        return this.owner;
    }

    public void setOwner(UUID userUUID){
        this.owner = userUUID;
    }
}
