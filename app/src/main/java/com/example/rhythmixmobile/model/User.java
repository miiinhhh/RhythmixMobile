package com.example.rhythmixmobile.model;

public class User {
    private int id;
    private String username;
    private String profileImageUrl;

    public User(int id, String username, String profileImageUrl) {
        this.id = id;
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getProfileImageUrl() { return profileImageUrl; }
}
