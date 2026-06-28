package com.example.rhythmixmobile.model;

import java.util.List;

public class PlaylistDetail {

    private String playlistId;
    private String name;
    private String description;
    private String coverImageUrl;

    private List<PlaylistTrack> tracks;

    public List<PlaylistTrack> getTracks() {
        return tracks;
    }
}