package com.example.rhythmixmobile.model;

public class FollowUser {
    public String id;
    public String userName;
    public String email;
    public String displayName;
    public String bio;
    public String avatarUrl;
    public String createdAt;

    public String getUserName() {
        return userName;
    }
    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}