package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.Album;
import com.example.rhythmixmobile.model.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AlbumApi {

    @GET("api/Albums/my-albums")
    Call<ApiResponse<List<Album>>> getMyAlbums();

}