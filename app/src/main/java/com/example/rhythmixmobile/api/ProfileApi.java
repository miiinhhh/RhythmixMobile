package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.AvatarUploadResponse;
import com.example.rhythmixmobile.model.UpdateProfileRequest;
import com.example.rhythmixmobile.model.UserProfile;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface ProfileApi {

    @GET("api/Profile/me")
    Call<ApiResponse<UserProfile>> getMyProfile();

    @GET("api/Profile/users")
    Call<ApiResponse<List<UserProfile>>> getUsers();

    @PUT("api/Profile/me")
    Call<ApiResponse<UserProfile>> updateProfile(
            @Body UpdateProfileRequest request
    );

    @Multipart
    @POST("api/Profile/me/avatar")
    Call<ApiResponse<AvatarUploadResponse>> uploadAvatar(
            @Part MultipartBody.Part file
    );
}
