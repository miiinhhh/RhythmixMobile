package com.example.rhythmixmobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        prefs = context.getSharedPreferences(
                "auth",
                Context.MODE_PRIVATE
        );
    }

    public void saveToken(String token) {
        prefs.edit().putString("token", token).apply();
    }

    public String getToken() {
        return prefs.getString("token", "");
    }

    public void clearToken() {
        prefs.edit().remove("token").apply();
    }
}