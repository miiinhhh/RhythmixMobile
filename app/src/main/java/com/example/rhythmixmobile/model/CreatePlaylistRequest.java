package com.example.rhythmixmobile.model;

public class CreatePlaylistRequest {

    private String name;
    private String description;
    private boolean isPublic;

    public CreatePlaylistRequest(String name,
                                 String description,
                                 boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPublic() {
        return isPublic;
    }
}