package com.example.rhythmixmobile.model;

public class UpdateProfileRequest {
    public String id;
    public String userName;
    public String displayName;
    public String bio;
    public String avatarUrl;

    public UpdateProfileRequest(String id, String userName, String displayName, String bio, String avatarUrl) {
        this.id = id;
        this.userName = userName;
        this.displayName = displayName;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }
}
