package com.example.rhythmixmobile.api;

import android.content.Context;

import com.example.rhythmixmobile.utils.Constants;
import com.example.rhythmixmobile.utils.TokenManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = Constants.BASE_URL;
//http://10.91.60.65:5269/
//http://10.0.2.2:5269/
    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context) {

        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor((Interceptor) chain -> {

                        TokenManager tokenManager = new TokenManager(context);

                        String token = tokenManager.getToken();

                        Request.Builder builder = chain.request().newBuilder();

                        if (token != null && !token.isEmpty()) {
                            builder.addHeader(
                                    "Authorization",
                                    "Bearer " + token
                            );
                        }

                        return chain.proceed(builder.build());

                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}