package com.example.rhythmixmobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.adapter.ShareAdapter;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.api.ShareApi;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.ShareItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InboxActivity extends AppCompatActivity {

    private TextView btnBack, tabInbox, tabOutbox, tvEmpty;
    private RecyclerView rvShares;

    private ShareApi shareApi;
    private ShareAdapter shareAdapter;

    private boolean inboxMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        initViews();
        setupRetrofit();
        setupRecyclerView();
        setupListeners();

        loadInbox();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tabInbox = findViewById(R.id.tabInbox);
        tabOutbox = findViewById(R.id.tabOutbox);
        tvEmpty = findViewById(R.id.tvEmpty);
        rvShares = findViewById(R.id.rvShares);
    }

    private void setupRetrofit() {
        shareApi = RetrofitClient
                .getInstance(this)
                .create(ShareApi.class);
    }

    private void setupRecyclerView() {
        shareAdapter = new ShareAdapter();
        rvShares.setLayoutManager(new LinearLayoutManager(this));
        rvShares.setAdapter(shareAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        tabInbox.setOnClickListener(v -> loadInbox());

        tabOutbox.setOnClickListener(v -> loadOutbox());
    }

    private void loadInbox() {
        inboxMode = true;
        updateTabs();

        shareApi.getInbox().enqueue(new Callback<ApiResponse<List<ShareItem>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<ShareItem>>> call,
                    Response<ApiResponse<List<ShareItem>>> response
            ) {
                handleShareResponse(response);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShareItem>>> call, Throwable t) {
                Log.e("SHARE", "Load inbox failed", t);
                Toast.makeText(InboxActivity.this, "Lỗi tải Inbox", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOutbox() {
        inboxMode = false;
        updateTabs();

        shareApi.getOutbox().enqueue(new Callback<ApiResponse<List<ShareItem>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<ShareItem>>> call,
                    Response<ApiResponse<List<ShareItem>>> response
            ) {
                handleShareResponse(response);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShareItem>>> call, Throwable t) {
                Log.e("SHARE", "Load outbox failed", t);
                Toast.makeText(InboxActivity.this, "Lỗi tải Outbox", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleShareResponse(Response<ApiResponse<List<ShareItem>>> response) {
        if (response.isSuccessful()
                && response.body() != null
                && response.body().isSuccess()
                && response.body().getData() != null) {

            List<ShareItem> shares = response.body().getData();

            shareAdapter.setData(shares, inboxMode);

            if (shares.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvShares.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvShares.setVisibility(View.VISIBLE);
            }

        } else {
            Toast.makeText(this, "Không tải được danh sách share", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTabs() {
        if (inboxMode) {
            tabInbox.setBackgroundColor(getColor(android.R.color.holo_green_light));
            tabInbox.setTextColor(getColor(android.R.color.black));

            tabOutbox.setBackgroundColor(getColor(android.R.color.transparent));
            tabOutbox.setTextColor(getColor(android.R.color.white));
        } else {
            tabOutbox.setBackgroundColor(getColor(android.R.color.holo_green_light));
            tabOutbox.setTextColor(getColor(android.R.color.black));

            tabInbox.setBackgroundColor(getColor(android.R.color.transparent));
            tabInbox.setTextColor(getColor(android.R.color.white));
        }
    }
}