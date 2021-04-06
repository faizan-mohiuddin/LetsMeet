package com.LetsMeet.LetsMeet.Root.Media;

import java.nio.file.Path;
import java.util.UUID;


public class Media {
    private Path path;
    private UUID uuid;
    private String type;

    public Media(UUID uuid, Path path, String type ) {
        this.path = path;
        this.uuid = uuid;
        this.type = type;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
