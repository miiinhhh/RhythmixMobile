package com.example.rhythmixmobile.model;

import com.google.gson.annotations.SerializedName;

public class RecentPlayHistory {

    @SerializedName("historyId")
    private String historyId;

    @SerializedName("mediaId")
    private String mediaId;

    @SerializedName("title")
    private String title;

    @SerializedName("mediaType")
    private String mediaType;

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    @SerializedName("filePath")
    private String filePath;

    @SerializedName("playedAt")
    private String playedAt;

    public String getHistoryId() { return historyId; }
    public String getMediaId() { return mediaId; }
    public String getTitle() { return title; }
    public String getMediaType() { return mediaType; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getFilePath() { return filePath; }
    public String getPlayedAt() { return playedAt; }
}