package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.ShareRequest;
import com.example.rhythmixmobile.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SocialApi {
    @GET("api/Social/suggested-users")
    Call<List<User>> getSuggestedUsers();

    @POST("api/Social/share")
    Call<Void> share(@Body ShareRequest request);

    @GET("api/Social/search-users")
    Call<List<User>> searchUsers(@Query("query") String query);
}
