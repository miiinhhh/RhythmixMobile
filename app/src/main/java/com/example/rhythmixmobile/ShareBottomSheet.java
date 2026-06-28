package com.example.rhythmixmobile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.R;
import com.example.rhythmixmobile.adapter.ShareUserAdapter;
import com.example.rhythmixmobile.api.FollowApi;
import com.example.rhythmixmobile.api.ProfileApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.api.ShareApi;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.CreateShareRequest;
import com.example.rhythmixmobile.model.FollowUser;
import com.example.rhythmixmobile.model.UserProfile;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_MEDIA_ID = "mediaId";

    private RecyclerView rvUsers;
    private EditText etSearch;
    private Button btnSend;

    private ShareUserAdapter adapter;

    private FollowApi followApi;
    private ShareApi shareApi;

    private UserProfile selectedUser;

    private String mediaId;
    private ProfileApi profileApi;

    public static ShareBottomSheet newInstance(String mediaId) {


        ShareBottomSheet sheet = new ShareBottomSheet();

        Bundle args = new Bundle();
        args.putString(ARG_MEDIA_ID, mediaId);

        sheet.setArguments(args);

        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.bottom_sheet_share,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mediaId = getArguments().getString(ARG_MEDIA_ID);

        rvUsers = view.findViewById(R.id.rvUsers);
        etSearch = view.findViewById(R.id.etSearch);
        btnSend = view.findViewById(R.id.btnSend);

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        followApi = RetrofitClient
                .getInstance(requireContext())
                .create(FollowApi.class);

        shareApi = RetrofitClient
                .getInstance(requireContext())
                .create(ShareApi.class);

        profileApi = RetrofitClient
                .getInstance(requireContext())
                .create(ProfileApi.class);

        adapter = new ShareUserAdapter(user -> {
            selectedUser = user;
        });

        rvUsers.setAdapter(adapter);

        loadUsers();

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {

                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSend.setOnClickListener(v -> shareSong());
    }

    private void loadUsers() {
        profileApi.getUsers()
                .enqueue(new Callback<ApiResponse<List<UserProfile>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<UserProfile>>> call,
                            Response<ApiResponse<List<UserProfile>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            adapter.setUsers(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<UserProfile>>> call,
                            Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void shareSong() {

        if (selectedUser == null) {

            Toast.makeText(
                    getContext(),
                    "Vui lòng chọn người nhận",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        CreateShareRequest request = new CreateShareRequest();

        request.setReceiverId(selectedUser.id);
        request.setMediaId(mediaId);
        request.setPlaylistId(null);
        request.setMessage("");

        shareApi.share(request)
                .enqueue(new Callback<ApiResponse<Object>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<Object>> call,
                            Response<ApiResponse<Object>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            Toast.makeText(
                                    getContext(),
                                    "Đã chia sẻ thành công",
                                    Toast.LENGTH_SHORT
                            ).show();

                            dismiss();

                        } else {

                            Toast.makeText(
                                    getContext(),
                                    "Chia sẻ thất bại",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<Object>> call,
                            Throwable t) {

                        Toast.makeText(
                                getContext(),
                                t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}