package com.example.rhythmixmobile.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Song implements Serializable {

    @SerializedName("mediaId")
    private String mediaId;

    @SerializedName("title")
    private String title;

    @SerializedName("artist")
    private String artist;

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    @SerializedName("description")
    private String description;

    @SerializedName("streamUrl")
    private String streamUrl;

    public String getMediaId() {
        return mediaId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getStreamUrl() {
        return streamUrl;
    }
    public Song(String mediaId, String title, String artist,
                String thumbnailUrl, String description, String streamUrl) {
        this.mediaId = mediaId;
        this.title = title;
        this.artist = artist;
        this.thumbnailUrl = thumbnailUrl;
        this.description = description;
        this.streamUrl = streamUrl;
    }
}