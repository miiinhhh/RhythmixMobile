package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.FollowCounts;
import com.example.rhythmixmobile.model.FollowUser;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface FollowApi {

    @GET("api/Follows/users/{userId}/counts")
    Call<ApiResponse<FollowCounts>> getFollowCounts(
            @Path("userId") String userId
    );
    @GET("api/Follows/users/{userId}/followers")
    Call<ApiResponse<List<FollowUser>>> getFollowers(
            @Path("userId") String userId
    );

    @GET("api/Follows/users/{userId}/following")
    Call<ApiResponse<List<FollowUser>>> getFollowing(
            @Path("userId") String userId
    );
}