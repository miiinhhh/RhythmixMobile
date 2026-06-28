package com.example.rhythmixmobile.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Playlist implements Serializable {

    @SerializedName("playlistId")
    private String playlistId;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("coverImageUrl")
    private String coverImageUrl;

    @SerializedName("isPublic")
    private boolean isPublic;
    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public boolean isPublic() {
        return isPublic;
    }
}