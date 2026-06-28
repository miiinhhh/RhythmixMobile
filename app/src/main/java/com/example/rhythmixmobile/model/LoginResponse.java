package com.example.rhythmixmobile.model;

public class LoginResponse {

    private boolean success;
    private UserData data;

    public boolean isSuccess() {
        return success;
    }

    public UserData getData() {
        return data;
    }
}
