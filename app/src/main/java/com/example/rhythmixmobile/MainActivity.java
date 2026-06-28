package com.example.rhythmixmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.adapter.PlaylistAdapter;
import com.example.rhythmixmobile.adapter.SongAdapter;
import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.Playlist;
import com.example.rhythmixmobile.model.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.rhythmixmobile.model.MediaDiscoveryResponse;
import com.example.rhythmixmobile.model.MediaItem;
import com.example.rhythmixmobile.utils.Constants;

import java.util.ArrayList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAiSuggested, rvYourPlaylists, rvPublicPlaylists, rvYourAlbums, rvAllSongs;
    private SongAdapter aiSuggestedAdapter;
    private SongAdapter allSongAdapter;

    private PlaylistAdapter yourPlaylistAdapter;
    private PlaylistAdapter publicPlaylistAdapter;
    private MusicApi musicApi;

    // Các View cho giao diện
    private CardView miniPlayer;
    private ImageView ivMiniThumb, btnMiniPlay;
    private TextView tvMiniTitle, tvMiniArtist;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 3. Xử lý Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupWindowInsets();
        initViews();
        setupRetrofit();
        setupBottomNavigation();
        loadData();

        // Xử lý nút Inbox
        findViewById(R.id.btnInbox).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InboxActivity.class);
            startActivity(intent);
        });

        // Xử lý nút Thông báo
        findViewById(R.id.btnNotifications).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            );
            return insets;
        });
    }

    private void initViews() {
        rvAiSuggested = findViewById(R.id.rvAiSuggested);
        rvYourPlaylists = findViewById(R.id.rvYourPlaylists);
        rvPublicPlaylists = findViewById(R.id.rvPublicPlaylists);
        rvYourAlbums = findViewById(R.id.rvYourAlbums);
        rvAllSongs = findViewById(R.id.rvAllSongs);

        rvAiSuggested.setLayoutManager(new LinearLayoutManager(this));
        rvYourPlaylists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPublicPlaylists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvYourAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvAllSongs.setLayoutManager(new LinearLayoutManager(this));

        // Mini Player
        miniPlayer = findViewById(R.id.miniPlayer);
        ivMiniThumb = findViewById(R.id.ivMiniThumb);
        tvMiniTitle = findViewById(R.id.tvMiniTitle);
        tvMiniArtist = findViewById(R.id.tvMiniArtist);
        btnMiniPlay = findViewById(R.id.btnMiniPlay);
        
        // Bottom Navigation
        bottomNav = findViewById(R.id.bottomNav);
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_library) {
                startActivity(new Intent(this, LibraryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void setupRetrofit() {
        musicApi = RetrofitClient
                .getInstance(this)
                .create(MusicApi.class);
    }

    private void loadData() {
        fetchDiscoveryHome();
        fetchMyPlaylists();
        fetchFeaturedPlaylists();
    }



    private void playSong(Song song) {
        miniPlayer.setVisibility(View.VISIBLE);
        tvMiniTitle.setText(song.getTitle());
        tvMiniArtist.setText(song.getArtist());
        
        Glide.with(this)
                .load(song.getThumbnailUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(ivMiniThumb);

        miniPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, SongDetailActivity.class);
            intent.putExtra("song", song);
            startActivity(intent);
        });
    }

    private void openPlaylist(Playlist playlist) {
        Intent intent = new Intent(this, PlaylistDetailActivity.class);
        intent.putExtra("playlist", playlist);
        startActivity(intent);
    }
    private void fetchDiscoveryHome() {
        musicApi.getDiscoveryMedia(1, 30)
                .enqueue(new Callback<MediaDiscoveryResponse>() {
                    @Override
                    public void onResponse(
                            Call<MediaDiscoveryResponse> call,
                            Response<MediaDiscoveryResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            List<MediaItem> mediaList = response.body().getData();

                            List<Song> aiSongs = new ArrayList<>();
                            List<Song> allSongs = new ArrayList<>();

                            for (int i = 0; i < mediaList.size(); i++) {
                                Song song = mapMediaItemToSong(mediaList.get(i));

                                if (i < 10) {
                                    aiSongs.add(song);
                                }

                                allSongs.add(song);
                            }

                            aiSuggestedAdapter =
                                    new SongAdapter(aiSongs, song -> playSong(song));

                            allSongAdapter =
                                    new SongAdapter(allSongs, song -> playSong(song));

                            rvAiSuggested.setAdapter(aiSuggestedAdapter);
                            rvAllSongs.setAdapter(allSongAdapter);

                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Không tải được nhạc trang chủ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MediaDiscoveryResponse> call, Throwable t) {
                        Log.e("HOME", "Discovery failed", t);
                        Toast.makeText(MainActivity.this,
                                "Lỗi kết nối trang chủ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private Song mapMediaItemToSong(MediaItem media) {
        String thumbnailUrl = media.getThumbnailUrl();
        String filePath = media.getFilePath();

        if (thumbnailUrl != null && !thumbnailUrl.startsWith("http")) {
            thumbnailUrl = Constants.BASE_URL + thumbnailUrl;
        }

        if (filePath != null && !filePath.startsWith("http")) {
            filePath = Constants.BASE_URL + filePath;
        }

        return new Song(
                media.getMediaId(),
                media.getTitle(),
                media.getArtistName(),
                thumbnailUrl,
                media.getDescription(),
                filePath
        );
    }
    private void fetchFeaturedPlaylists() {

        musicApi.getPublicPlaylists()
                .enqueue(new Callback<ApiResponse<List<Playlist>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Playlist>>> call,
                            Response<ApiResponse<List<Playlist>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            publicPlaylistAdapter =
                                    new PlaylistAdapter(
                                            response.body().getData(),
                                            playlist -> openPlaylist(playlist)
                                    );

                            rvPublicPlaylists.setAdapter(publicPlaylistAdapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Playlist>>> call,
                            Throwable t) {

                        Log.e("PLAYLIST", "Load public playlist failed", t);
                    }
                });
    }
    private void fetchMyPlaylists() {

        musicApi.getMyPlaylists()
                .enqueue(new Callback<ApiResponse<List<Playlist>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Playlist>>> call,
                            Response<ApiResponse<List<Playlist>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            yourPlaylistAdapter =
                                    new PlaylistAdapter(
                                            response.body().getData(),
                                            playlist -> openPlaylist(playlist)
                                    );

                            rvYourPlaylists.setAdapter(yourPlaylistAdapter);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Playlist>>> call,
                            Throwable t) {

                        Log.e("PLAYLIST", "Load my playlist failed", t);
                    }
                });
    }
}
