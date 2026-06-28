package com.example.rhythmixmobile.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Album implements Serializable {

    @SerializedName("albumId")
    private String albumId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("coverImageUrl")
    private String coverImageUrl;

    @SerializedName("artistName")
    private String artistName;

    @SerializedName("trackCount")
    private int trackCount;

    public String getAlbumId() { return albumId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getArtistName() { return artistName; }
    public int getTrackCount() { return trackCount; }
}