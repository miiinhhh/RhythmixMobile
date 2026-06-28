package com.example.rhythmixmobile.model;

import com.google.gson.annotations.SerializedName;

public class FollowCounts {

    @SerializedName("followersCount")
    private int followersCount;

    @SerializedName("followingCount")
    private int followingCount;

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }
}