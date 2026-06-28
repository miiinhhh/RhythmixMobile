package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.RecentPlayHistory;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InteractionApi {

    @GET("api/Interactions/favorites")
    Call<ApiResponse<List<String>>> getFavoriteIds();

    @GET("api/Interactions/recent-history")
    Call<ApiResponse<List<RecentPlayHistory>>> getRecentHistory(@Query("count") int count);
    @POST("api/Interactions/favorite/{mediaId}")
    Call<ApiResponse<Object>> toggleFavorite(
            @Path("mediaId") String mediaId
    );
}
