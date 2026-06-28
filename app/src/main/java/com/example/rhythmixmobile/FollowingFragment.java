package com.example.rhythmixmobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.api.FollowApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.FollowUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingFragment extends Fragment {

    private RecyclerView rvFollowing;
    private FollowUserAdapter adapter;
    private final List<FollowUser> users = new ArrayList<>();

    public FollowingFragment() {
        super(R.layout.fragment_following);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFollowing = view.findViewById(R.id.rvFollowing);
        rvFollowing.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new FollowUserAdapter(requireContext(), users);
        rvFollowing.setAdapter(adapter);

        String userId = getArguments() != null
                ? getArguments().getString("userId")
                : null;

        loadFollowing(userId);
    }

    private void loadFollowing(String userId) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(requireContext(), "Không có userId", Toast.LENGTH_SHORT).show();
            return;
        }

        FollowApi followApi = RetrofitClient
                .getInstance(requireContext())
                .create(FollowApi.class);

        followApi.getFollowing(userId)
                .enqueue(new Callback<ApiResponse<List<FollowUser>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<FollowUser>>> call,
                            Response<ApiResponse<List<FollowUser>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            users.clear();
                            users.addAll(response.body().getData());
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.e("FOLLOWING", "Load failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<FollowUser>>> call,
                            Throwable t) {
                        Log.e("FOLLOWING", "API failed", t);
                    }
                });
    }
}