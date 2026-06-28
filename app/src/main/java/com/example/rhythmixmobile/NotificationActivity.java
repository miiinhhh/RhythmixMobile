package com.example.rhythmixmobile;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.adapter.NotificationAdapter;
import com.example.rhythmixmobile.api.NotificationApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private NotificationApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        api = RetrofitClient.getInstance(this)
                .create(NotificationApi.class);

        loadNotifications();
    }

    private void loadNotifications() {
        api.getNotifications()
                .enqueue(new Callback<ApiResponse<List<Notification>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Notification>>> call,
                            Response<ApiResponse<List<Notification>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            adapter = new NotificationAdapter(response.body().getData());
                            rvNotifications.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Notification>>> call,
                            Throwable t) {

                        Toast.makeText(
                                NotificationActivity.this,
                                "Không tải được thông báo",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}