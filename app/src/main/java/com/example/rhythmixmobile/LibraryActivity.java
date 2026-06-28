package com.example.rhythmixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.rhythmixmobile.model.MediaDiscoveryResponse;
import com.example.rhythmixmobile.adapter.LibraryAdapter;
import com.example.rhythmixmobile.adapter.PlaylistAdapter;
import com.example.rhythmixmobile.api.AlbumApi;
import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.api.PlaylistApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.Album;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.LibraryItem;
import com.example.rhythmixmobile.model.MediaItem;
import com.example.rhythmixmobile.model.Playlist;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.model.Song;
import com.example.rhythmixmobile.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LibraryActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private RecyclerView rvLibrary;
    private PlaylistApi playlistApi;
    private LibraryAdapter libraryAdapter;
    private MusicApi musicApi;
    private AlbumApi albumApi;
    private List<LibraryItem> libraryItems = new ArrayList<>();
    private Button btnAll, btnPlaylists, btnAlbums;
    private Button btnCreatePlaylist, btnUploadMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_library);

        initViews();
        selectFilter(btnAll);
        setupButtons();
        setupBottomNavigation();
        setupRecyclerView();
        setupApi();
        loadAllLibrary();
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);
        rvLibrary = findViewById(R.id.rvLibrary);

        btnAll = findViewById(R.id.btnAll);
        btnPlaylists = findViewById(R.id.btnPlaylists);
        btnAlbums = findViewById(R.id.btnAlbums);

        btnCreatePlaylist = findViewById(R.id.btnCreatePlaylist);
        btnUploadMusic = findViewById(R.id.btnUploadMusic);
    }

    private void setupButtons() {
        btnCreatePlaylist.setOnClickListener(v -> {
            startActivity(new Intent(this, CreatePlaylistActivity.class));
        });

        btnUploadMusic.setOnClickListener(v -> {
            startActivity(new Intent(this, UploadMusicActivity.class));
        });

        btnAll.setOnClickListener(v -> {
            selectFilter(btnAll);
            loadAllLibrary();
        });

        btnPlaylists.setOnClickListener(v -> {
            selectFilter(btnPlaylists);

            libraryItems.clear();

            loadPlaylists();
        });

        btnAlbums.setOnClickListener(v -> {
            selectFilter(btnAlbums);

            libraryItems.clear();

            loadAlbums();
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_library);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_library) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }
    private void setupRecyclerView() {

        rvLibrary.setLayoutManager(new LinearLayoutManager(this));

        libraryAdapter = new LibraryAdapter(
                this,
                libraryItems
        );

        rvLibrary.setAdapter(libraryAdapter);
    }
    private void setupApi() {

        playlistApi = RetrofitClient
                .getInstance(this)
                .create(PlaylistApi.class);

        musicApi = RetrofitClient
                .getInstance(this)
                .create(MusicApi.class);

        albumApi = RetrofitClient
                .getInstance(this)
                .create(AlbumApi.class);
    }
    private void loadMyPlaylists() {

        playlistApi.getMyPlaylists().enqueue(
                new Callback<ApiResponse<List<Playlist>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Playlist>>> call,
                            Response<ApiResponse<List<Playlist>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            libraryItems.clear();

                            for (Playlist playlist : response.body().getData()) {
                                libraryItems.add(new LibraryItem(playlist));
                            }

                            libraryAdapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Playlist>>> call,
                            Throwable t) {

                    }
                });
    }
    private void selectFilter(Button selectedButton) {
        btnAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#2A2A2A")));
        btnPlaylists.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#2A2A2A")));
        btnAlbums.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#2A2A2A")));

        btnAll.setTextColor(android.graphics.Color.WHITE);
        btnPlaylists.setTextColor(android.graphics.Color.WHITE);
        btnAlbums.setTextColor(android.graphics.Color.WHITE);

        selectedButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#1DB954")));
        selectedButton.setTextColor(android.graphics.Color.BLACK);
    }
    private void loadAllLibrary() {

        libraryItems.clear();

        loadPlaylists();
        loadSongs();
        loadAlbums();
    }
    private void loadPlaylists() {

        playlistApi.getMyPlaylists().enqueue(
                new Callback<ApiResponse<List<Playlist>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Playlist>>> call,
                            Response<ApiResponse<List<Playlist>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            for (Playlist playlist : response.body().getData()) {
                                libraryItems.add(new LibraryItem(playlist));
                            }

                            libraryAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Playlist>>> call,
                            Throwable t) {

                    }
                });
    }
    private void loadSongs() {

        musicApi.getMyMedia(1,100)
                .enqueue(new Callback<MediaDiscoveryResponse>() {

                    @Override
                    public void onResponse(
                            Call<MediaDiscoveryResponse> call,
                            Response<MediaDiscoveryResponse> response) {

                        if (response.isSuccessful()
                                && response.body()!=null
                                && response.body().isSuccess()) {

                            for(MediaItem item : response.body().getData()){

                                Song song = new Song(
                                        item.getMediaId(),
                                        item.getTitle(),
                                        item.getArtistName(),
                                        Constants.BASE_URL + item.getThumbnailUrl(),
                                        item.getDescription(),
                                        Constants.BASE_URL + item.getFilePath()
                                );

                                libraryItems.add(new LibraryItem(song));
                            }

                            libraryAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(
                            Call<MediaDiscoveryResponse> call,
                            Throwable t) {

                    }
                });
    }
    private void loadAlbums(){

        albumApi.getMyAlbums()
                .enqueue(new Callback<ApiResponse<List<Album>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Album>>> call,
                            Response<ApiResponse<List<Album>>> response) {

                        if(response.isSuccessful()
                                && response.body()!=null
                                && response.body().isSuccess()){

                            for(Album album : response.body().getData()){

                                libraryItems.add(new LibraryItem(album));
                            }

                            libraryAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Album>>> call,
                            Throwable t) {

                    }
                });

    }
}