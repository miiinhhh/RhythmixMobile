package com.example.rhythmixmobile.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {

    @SerializedName("media")
    private List<Song> media;

    @SerializedName("playlists")
    private List<Playlist> playlists;

    @SerializedName("albums")
    private List<Album> albums;

    public List<Song> getMedia() {
        return media;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public List<Album> getAlbums() {
        return albums;
    }
}
