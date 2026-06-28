package com.example.rhythmixmobile.model;

import java.util.List;

public class MediaDiscoveryResponse {

    private boolean success;
    private List<MediaItem> data;

    public boolean isSuccess() {
        return success;
    }

    public List<MediaItem> getData() {
        return data;
    }
}