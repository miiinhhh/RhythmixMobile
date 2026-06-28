package com.example.rhythmixmobile.model;

public class AddTrackRequest {

    private String mediaId;
    private int sortOrder;

    public AddTrackRequest(String mediaId, int sortOrder) {
        this.mediaId = mediaId;
        this.sortOrder = sortOrder;
    }

    public String getMediaId() {
        return mediaId;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}