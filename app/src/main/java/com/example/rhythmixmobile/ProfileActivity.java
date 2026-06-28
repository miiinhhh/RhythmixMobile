package com.example.rhythmixmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rhythmixmobile.model.RecentPlayHistory;
import com.example.rhythmixmobile.api.ProfileApi;
import com.example.rhythmixmobile.model.MediaItem;
import com.example.rhythmixmobile.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.UserProfile;
import com.example.rhythmixmobile.api.FollowApi;
import com.example.rhythmixmobile.model.FollowCounts;
import com.example.rhythmixmobile.adapter.SongAdapter;
import com.example.rhythmixmobile.api.InteractionApi;
import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.model.MediaResponse;
import com.example.rhythmixmobile.model.Song;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private Button btnEditProfile, btnLogout;
    private TextView btnPublicPlaylists, btnLiked, btnPlayHistory;
    private View tabIndicator;
    private BottomNavigationView bottomNavigation;
    private ProfileApi profileApi;
    private FollowApi followApi;
    private String currentUserId;
    private ImageView imgAvatar;
    private TextView txtName, txtBio;
    private RecyclerView rvProfileContent;
    private SongAdapter songAdapter;
    private List<Song> likedSongs = new ArrayList<>();

    private InteractionApi interactionApi;
    private MusicApi musicApi;

    private TextView txtFollowInfo, txtSectionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtBio = findViewById(R.id.txtBio);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        btnPublicPlaylists = findViewById(R.id.btnPublicPlaylists);
        btnLiked = findViewById(R.id.btnLiked);
        btnPlayHistory = findViewById(R.id.btnPlayHistory);
        tabIndicator = findViewById(R.id.tabIndicator);

        txtFollowInfo = findViewById(R.id.txtFollowInfo);
        txtFollowInfo.setText("Đang tải follow...");
        txtSectionTitle = findViewById(R.id.txtSectionTitle);

        rvProfileContent = findViewById(R.id.rvProfileContent);
        rvProfileContent.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        rvProfileContent.setNestedScrollingEnabled(false);
        rvProfileContent.setHasFixedSize(false);

        songAdapter = new SongAdapter(likedSongs, song -> {
            Intent intent = new Intent(ProfileActivity.this, SongDetailActivity.class);
            intent.putExtra("song", song);
            startActivity(intent);
        });

        rvProfileContent.setAdapter(songAdapter);

        setupTabs();

        bottomNavigation = findViewById(R.id.bottomNavigation);

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });

        txtFollowInfo.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FollowActivity.class);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("tab", 0);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences("RhythmixPref", MODE_PRIVATE);
            pref.edit().remove("token").apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        profileApi = RetrofitClient
                .getInstance(this)
                .create(ProfileApi.class);

        followApi = RetrofitClient
                .getInstance(this)
                .create(FollowApi.class);

        interactionApi = RetrofitClient
                .getInstance(this)
                .create(InteractionApi.class);

        musicApi = RetrofitClient
                .getInstance(this)
                .create(MusicApi.class);

        loadProfile();

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            }

            if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                return true;
            }

            if (id == R.id.nav_library) {
                startActivity(new Intent(this, LibraryActivity.class));
                finish();
                return true;
            }

            if (id == R.id.nav_profile) {
                return true;
            }

            return false;
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (profileApi != null) {
            loadProfile();
        }
    }

    private void loadProfile() {
        profileApi.getMyProfile().enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    UserProfile profile = response.body().getData();
                    currentUserId = profile.id;

                    loadFollowCounts(profile.id);

                    txtName.setText(
                            profile.displayName != null && !profile.displayName.isEmpty()
                                    ? profile.displayName
                                    : profile.userName
                    );

                    txtBio.setText(
                            profile.bio != null && !profile.bio.isEmpty()
                                    ? profile.bio
                                    : "Music lover"
                    );

                    // Gọi follow count ở đây
                    loadFollowCounts(profile.id);

                    if (profile.avatarUrl != null && !profile.avatarUrl.isEmpty()) {
                        String imageUrl = Constants.BASE_URL + profile.avatarUrl;

                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .circleCrop()
                                .into(imgAvatar);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Không tải được hồ sơ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Log.e("PROFILE_ERROR", "Load profile failed", t);
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadFollowCounts(String userId){

        Log.d("FOLLOW", "UserId = " + userId);

        followApi.getFollowCounts(userId)
                .enqueue(new Callback<ApiResponse<FollowCounts>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<FollowCounts>> call,
                            Response<ApiResponse<FollowCounts>> response) {

                        Log.d("FOLLOW", "HTTP = " + response.code());

                        if (response.body() != null) {
                            Log.d("FOLLOW", "Success = " + response.body().isSuccess());
                        }

                        if(response.isSuccessful()
                                && response.body()!=null
                                && response.body().isSuccess()){

                            FollowCounts counts = response.body().getData();

                            Log.d("FOLLOW",
                                    counts.getFollowersCount() + " / "
                                            + counts.getFollowingCount());

                            txtFollowInfo.setText(
                                    counts.getFollowersCount()
                                            + " Followers • "
                                            + counts.getFollowingCount()
                                            + " Following"
                            );

                        } else {

                            txtFollowInfo.setText("Load failed");
                        }

                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<FollowCounts>> call,
                            Throwable t) {

                        Log.e("FOLLOW", "ERROR", t);

                        txtFollowInfo.setText("API Error");
                    }
                });



    }
    private void setupTabs() {
        btnPublicPlaylists.post(() -> {
            int tabWidth = btnPublicPlaylists.getWidth();

            tabIndicator.getLayoutParams().width = tabWidth;
            tabIndicator.requestLayout();

            selectTab(0);
        });

        btnPublicPlaylists.setOnClickListener(v -> {
            selectTab(0);
            txtSectionTitle.setText("Public Playlists");
        });

        btnLiked.setOnClickListener(v -> {
            selectTab(1);
            txtSectionTitle.setText("Liked Songs");
            loadLikedSongs();
        });

        btnPlayHistory.setOnClickListener(v -> {
            selectTab(2);
            txtSectionTitle.setText("Play History");
            loadPlayHistory();
        });
    }

    private void selectTab(int index) {
        int tabWidth = btnPublicPlaylists.getWidth();

        tabIndicator.animate()
                .translationX(index * tabWidth)
                .setDuration(220)
                .start();

        btnPublicPlaylists.setTextColor(index == 0 ? 0xFF000000 : 0xFFFFFFFF);
        btnLiked.setTextColor(index == 1 ? 0xFF000000 : 0xFFFFFFFF);
        btnPlayHistory.setTextColor(index == 2 ? 0xFF000000 : 0xFFFFFFFF);
    }
    private void loadLikedSongs() {
        likedSongs.clear();
        songAdapter.notifyDataSetChanged();

        interactionApi.getFavoriteIds()
                .enqueue(new Callback<ApiResponse<List<String>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<String>>> call,
                            Response<ApiResponse<List<String>>> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            List<String> favoriteIds = response.body().getData();

                            if (favoriteIds.isEmpty()) {
                                Toast.makeText(ProfileActivity.this,
                                        "Bạn chưa thích bài hát nào",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (String mediaId : favoriteIds) {
                                loadMediaById(mediaId);
                            }

                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Không tải được danh sách yêu thích",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<String>>> call,
                            Throwable t
                    ) {
                        Log.e("LIKED", "Load liked failed", t);
                        Toast.makeText(ProfileActivity.this,
                                "Lỗi kết nối liked",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadMediaById(String mediaId) {
        musicApi.getMediaById(mediaId)
                .enqueue(new Callback<MediaResponse>() {
                    @Override
                    public void onResponse(
                            Call<MediaResponse> call,
                            Response<MediaResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            MediaItem media = response.body().getData();

                            Song song = new Song(
                                    media.getMediaId(),
                                    media.getTitle(),
                                    media.getArtistName(),
                                    Constants.BASE_URL + media.getThumbnailUrl(),
                                    media.getDescription(),
                                    Constants.BASE_URL + media.getFilePath()
                            );

                            likedSongs.add(song);
                            songAdapter.notifyItemInserted(likedSongs.size() - 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<MediaResponse> call, Throwable t) {
                        Log.e("LIKED", "Load media failed: " + mediaId, t);
                    }
                });
    }
    private void loadPlayHistory() {
        likedSongs.clear();
        songAdapter.notifyDataSetChanged();

        rvProfileContent.post(() -> {
            songAdapter.notifyDataSetChanged();
            rvProfileContent.requestLayout();
        });

        interactionApi.getRecentHistory(10)
                .enqueue(new Callback<ApiResponse<List<RecentPlayHistory>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<RecentPlayHistory>>> call,
                            Response<ApiResponse<List<RecentPlayHistory>>> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            List<RecentPlayHistory> historyList = response.body().getData();

                            Log.d("HISTORY", "History size = " + historyList.size());

                            for (RecentPlayHistory item : historyList) {
                                Log.d("HISTORY", item.getTitle());
                            }

                            if (historyList.isEmpty()) {
                                Toast.makeText(ProfileActivity.this,
                                        "Bạn chưa nghe bài hát nào",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (RecentPlayHistory item : historyList) {
                                Song song = new Song(
                                        item.getMediaId(),
                                        item.getTitle(),
                                        "Unknown Artist",
                                        Constants.BASE_URL + item.getThumbnailUrl(),
                                        "",
                                        Constants.BASE_URL + item.getFilePath()
                                );

                                likedSongs.add(song);
                            }

                            songAdapter.notifyDataSetChanged();

                            Log.d("HISTORY", "History size = " + historyList.size());
                            Log.d("HISTORY", "likedSongs size = " + likedSongs.size());
                            Log.d("HISTORY", "Adapter count = " + songAdapter.getItemCount());

                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Không tải được lịch sử nghe",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<RecentPlayHistory>>> call,
                            Throwable t
                    ) {
                        Log.e("HISTORY", "Load history failed", t);
                        Toast.makeText(ProfileActivity.this,
                                "Lỗi kết nối play history",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
