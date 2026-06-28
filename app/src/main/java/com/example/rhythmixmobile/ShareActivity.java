package com.example.rhythmixmobile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.adapter.SearchUserAdapter;
import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.api.SocialApi;
import com.example.rhythmixmobile.model.MediaResponse;
import com.example.rhythmixmobile.model.Playlist;
import com.example.rhythmixmobile.model.ShareRequest;
import com.example.rhythmixmobile.model.User;
import com.example.rhythmixmobile.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etSearchUser, etMessage;
    private RecyclerView rvUsers;
    private Button btnSendShare;
    
    private LinearLayout llPreview;
    private ImageView ivPreviewThumb;
    private TextView tvPreviewTitle, tvPreviewSubtitle;

    private SearchUserAdapter adapter;
    private SocialApi socialApi;
    private MusicApi musicApi;
    
    private String mediaId;
    private String playlistId;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mediaId = getIntent().getStringExtra("songId");
        playlistId = getIntent().getStringExtra("playlistId");

        initViews();
        setupRetrofit();
        setupRecyclerView();
        setupSearch();
        loadPreviewData();
        loadSuggestedUsers();

        btnBack.setOnClickListener(v -> finish());
        btnSendShare.setOnClickListener(v -> sendShare());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearchUser = findViewById(R.id.etSearchUser);
        etMessage = findViewById(R.id.etMessage);
        rvUsers = findViewById(R.id.rvUsers);
        btnSendShare = findViewById(R.id.btnSendShare);
        
        llPreview = findViewById(R.id.llPreview);
        ivPreviewThumb = findViewById(R.id.ivPreviewThumb);
        tvPreviewTitle = findViewById(R.id.tvPreviewTitle);
        tvPreviewSubtitle = findViewById(R.id.tvPreviewSubtitle);
    }

    private void setupRetrofit() {
        socialApi = RetrofitClient.getInstance(this).create(SocialApi.class);
        musicApi = RetrofitClient.getInstance(this).create(MusicApi.class);
    }

    private void setupRecyclerView() {
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchUserAdapter(new ArrayList<>(), user -> {
            selectedUser = user;
            btnSendShare.setEnabled(true);
            btnSendShare.setText("Gửi cho " + user.getUsername());
        });
        rvUsers.setAdapter(adapter);
    }

    private void loadPreviewData() {
        if (mediaId != null) {
            musicApi.getMediaById(mediaId).enqueue(new Callback<MediaResponse>() {
                @Override
                public void onResponse(Call<MediaResponse> call, Response<MediaResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        tvPreviewTitle.setText(response.body().getData().getTitle());
                        tvPreviewSubtitle.setText(response.body().getData().getArtistName());
                        Glide.with(ShareActivity.this)
                                .load(Constants.IMAGE_BASE_URL + response.body().getData().getThumbnailUrl())
                                .placeholder(R.mipmap.ic_launcher)
                                .into(ivPreviewThumb);
                    }
                }
                @Override
                public void onFailure(Call<MediaResponse> call, Throwable t) {}
            });
        } else if (playlistId != null) {
            // Giả sử có API lấy playlist detail hoặc lấy từ intent object
            tvPreviewTitle.setText("Đang tải playlist...");
            // Ở đây có thể lấy playlist object từ Intent nếu được truyền qua
        }
    }

    private void setupSearch() {
        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchUsers(s.toString());
                } else {
                    loadSuggestedUsers();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadSuggestedUsers() {
        socialApi.getSuggestedUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setUsers(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {}
        });
    }

    private void searchUsers(String query) {
        socialApi.searchUsers(query).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setUsers(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {}
        });
    }

    private void sendShare() {
        if (selectedUser == null) return;

        String message = etMessage.getText().toString();
        // ID user trong hệ thống mới thường là String (GUID), check User model
        ShareRequest request = new ShareRequest(String.valueOf(selectedUser.getId()), mediaId, playlistId, message);
        
        btnSendShare.setEnabled(false);
        btnSendShare.setText("Đang gửi...");

        socialApi.share(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShareActivity.this, "Đã chia sẻ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ShareActivity.this, "Gửi thất bại", Toast.LENGTH_SHORT).show();
                    btnSendShare.setEnabled(true);
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ShareActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                btnSendShare.setEnabled(true);
            }
        });
    }
}
