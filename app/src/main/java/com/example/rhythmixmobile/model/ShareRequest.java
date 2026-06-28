package com.example.rhythmixmobile.model;

import com.google.gson.annotations.SerializedName;

public class ShareRequest {
    @SerializedName("receiverId")
    private String receiverId;
    
    @SerializedName("mediaId")
    private String mediaId;
    
    @SerializedName("playlistId")
    private String playlistId;
    
    @SerializedName("message")
    private String message;

    public ShareRequest(String receiverId, String mediaId, String playlistId, String message) {
        this.receiverId = receiverId;
        this.mediaId = mediaId;
        this.playlistId = playlistId;
        this.message = message;
    }

    public String getReceiverId() { return receiverId; }
    public String getMediaId() { return mediaId; }
    public String getPlaylistId() { return playlistId; }
    public String getMessage() { return message; }
}
