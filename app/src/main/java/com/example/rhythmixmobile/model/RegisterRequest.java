package com.example.rhythmixmobile.model;

public class RegisterRequest {
    public String email;
    public String userName;
    public String password;
    public String displayName;
    public String bio;
    public String avatarUrl;

    public RegisterRequest(String email, String userName, String password) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.displayName = userName;
        this.bio = "";
        this.avatarUrl = "";
    }
}