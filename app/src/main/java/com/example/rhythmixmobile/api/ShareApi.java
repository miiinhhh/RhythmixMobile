package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.CreateShareRequest;
import com.example.rhythmixmobile.model.ShareItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ShareApi {

    @POST("api/Shares")
    Call<ApiResponse<Object>> share(
            @Body CreateShareRequest request
    );

    @GET("api/Shares/inbox")
    Call<ApiResponse<List<ShareItem>>> getInbox();

    @GET("api/Shares/outbox")
    Call<ApiResponse<List<ShareItem>>> getOutbox();
}