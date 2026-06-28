package com.example.rhythmixmobile.api;


import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.CreatePlaylistRequest;
import com.example.rhythmixmobile.model.Playlist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PlaylistApi {

    @GET("api/Playlists/user/{userId}")
    Call<ApiResponse<List<Playlist>>> getUserPlaylists(
            @Path("userId") String userId
    );

    @POST("api/Playlists")
    Call<ApiResponse<Playlist>> createPlaylist(
            @Body CreatePlaylistRequest request
    );
    @GET("api/Playlists/my-playlists")
    Call<ApiResponse<List<Playlist>>> getMyPlaylists();
}
