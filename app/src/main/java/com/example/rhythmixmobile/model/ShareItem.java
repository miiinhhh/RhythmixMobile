package com.example.rhythmixmobile.model;

public class ShareItem {
    private String id;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String receiverName;
    private String mediaId;
    private String mediaTitle;
    private String mediaType;
    private String playlistId;
    private String playlistName;
    private String message;
    private String sharedAt;

    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getReceiverId() { return receiverId; }
    public String getReceiverName() { return receiverName; }
    public String getMediaId() { return mediaId; }
    public String getMediaTitle() { return mediaTitle; }
    public String getMediaType() { return mediaType; }
    public String getPlaylistId() { return playlistId; }
    public String getPlaylistName() { return playlistName; }
    public String getMessage() { return message; }
    public String getSharedAt() { return sharedAt; }

    public boolean isSongShare() {
        return mediaId != null && !mediaId.isEmpty();
    }

    public String getTitle() {
        if (isSongShare()) return mediaTitle;
        return playlistName;
    }
    public String getDisplayTitle() {
        return isSongShare() ? mediaTitle : playlistName;
    }

    public String getDisplayType() {
        return isSongShare() ? "Bài hát" : "Playlist";
    }
}