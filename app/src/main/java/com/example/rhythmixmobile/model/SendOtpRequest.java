package com.example.rhythmixmobile.model;

public class SendOtpRequest {
    public String email;
    public String userName;

    public SendOtpRequest(String email, String userName) {
        this.email = email;
        this.userName = userName;
    }
}