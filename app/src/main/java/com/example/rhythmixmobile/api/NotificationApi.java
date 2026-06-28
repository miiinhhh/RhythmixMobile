package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotificationApi {

    @GET("api/Notifications")
    Call<ApiResponse<List<Notification>>> getNotifications();

    @PUT("api/Notifications/{id}/read")
    Call<ApiResponse<Object>> markAsRead(
            @Path("id") String id
    );
}
