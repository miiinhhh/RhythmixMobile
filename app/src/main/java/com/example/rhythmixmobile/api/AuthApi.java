package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.LoginRequest;
import com.example.rhythmixmobile.model.LoginResponse;
import com.example.rhythmixmobile.model.RegisterRequest;
import com.example.rhythmixmobile.model.SendOtpRequest;
import com.example.rhythmixmobile.model.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("api/Auth/login")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );

    @POST("api/Auth/register")
    Call<LoginResponse> register(
            @Body RegisterRequest request
    );
    @POST("api/Auth/register/send-otp")
    Call<LoginResponse> sendRegisterOtp(@Body SendOtpRequest request);
    @POST("api/Auth/register/verify-otp")
    Call<LoginResponse> verifyRegisterOtp(@Body VerifyOtpRequest request);
}
