package com.example.rhythmixmobile.model;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private String type;

    @SerializedName("content")
    private String content;

    @SerializedName("isRead")
    private boolean isRead;

    @SerializedName("createdAt")
    private String createdAt;

    public String getId() { return id; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
}
