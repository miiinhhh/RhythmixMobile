package com.example.rhythmixmobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.rhythmixmobile.adapter.SearchAlbumAdapter;
import com.example.rhythmixmobile.adapter.SearchArtistAdapter;
import com.example.rhythmixmobile.adapter.SearchPlaylistAdapter;
import com.example.rhythmixmobile.adapter.SearchSongAdapter;
import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.Artist;
import com.example.rhythmixmobile.model.SearchResponse;
import com.example.rhythmixmobile.model.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView rvSongs, rvPlaylists;
    private TextView tvSongsHeader, tvPlaylistsHeader;
    private LinearLayout llDiscovery, llSearchResults;
    private BottomNavigationView bottomNav;
    private RecyclerView rvAlbums;
    private TextView tvAlbumsHeader;
    private RecyclerView rvArtists;
    private TextView tvArtistsHeader;
    private SearchArtistAdapter artistAdapter;
    private SearchAlbumAdapter albumAdapter;
    private SearchSongAdapter songAdapter;
    private SearchPlaylistAdapter playlistAdapter;
    
    private MusicApi musicApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        setupWindowInsets();
        initViews();
        styleSearchView();
        setupAdapters();
        setupRetrofit();
        setupSearch();
        setupBottomNavigation();
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
        searchView = findViewById(R.id.searchView);

        rvSongs = findViewById(R.id.rvSongsSearch);
        rvPlaylists = findViewById(R.id.rvPlaylistsSearch);
        rvAlbums = findViewById(R.id.rvAlbumsSearch);
        rvArtists = findViewById(R.id.rvArtistsSearch);

        tvSongsHeader = findViewById(R.id.tvSongsHeader);
        tvPlaylistsHeader = findViewById(R.id.tvPlaylistsHeader);
        tvAlbumsHeader = findViewById(R.id.tvAlbumsHeader);
        tvArtistsHeader = findViewById(R.id.tvArtistsHeader);

        llDiscovery = findViewById(R.id.llDiscovery);
        llSearchResults = findViewById(R.id.llSearchResults);

        bottomNav = findViewById(R.id.bottomNav);

        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvPlaylists.setLayoutManager(new LinearLayoutManager(this));
        rvAlbums.setLayoutManager(new LinearLayoutManager(this));
        rvArtists.setLayoutManager(new LinearLayoutManager(this));
    }

    private void styleSearchView() {
        EditText searchEditText =
                searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        if (searchEditText != null) {
            searchEditText.setTextColor(Color.WHITE);
            searchEditText.setHintTextColor(Color.GRAY);

            searchEditText.setHighlightColor(Color.WHITE);
            searchEditText.setLinkTextColor(Color.WHITE);
        }
    }

    private void setupAdapters() {

        songAdapter = new SearchSongAdapter(
                new ArrayList<>(),
                song -> {
                    Intent intent = new Intent(this, SongDetailActivity.class);
                    intent.putExtra("song", song);
                    startActivity(intent);
                });

        playlistAdapter = new SearchPlaylistAdapter(
                new ArrayList<>(),
                playlist -> {
                    Intent intent = new Intent(this, PlaylistDetailActivity.class);
                    intent.putExtra("playlist", playlist);
                    startActivity(intent);
                });

        albumAdapter = new SearchAlbumAdapter(
                new ArrayList<>(),
                album -> {

                    // Chưa có AlbumDetailActivity
                    // Tạm thời để trống
                    // Sau sẽ mở AlbumDetailActivity

                });
        artistAdapter = new SearchArtistAdapter(
                new ArrayList<>(),
                artist -> {
                    // Sau này mở ArtistDetailActivity
                });

        rvSongs.setAdapter(songAdapter);
        rvPlaylists.setAdapter(playlistAdapter);
        rvAlbums.setAdapter(albumAdapter);
        rvArtists.setAdapter(artistAdapter);
    }

    private void setupRetrofit() {
        musicApi = RetrofitClient
                .getInstance(this)
                .create(MusicApi.class);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    performSearch(query);
                    searchArtists(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    llDiscovery.setVisibility(View.GONE);
                    llSearchResults.setVisibility(View.VISIBLE);

                    performSearch(newText);
                    searchArtists(newText);

                } else if (newText.isEmpty()) {
                    llDiscovery.setVisibility(View.VISIBLE);
                    llSearchResults.setVisibility(View.GONE);
                    clearResults();
                }
                return true;
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_search);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else if (id == R.id.nav_library) {
                startActivity(new Intent(this, LibraryActivity.class));
                overridePendingTransition(0, 0);
                finish();
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

    private void performSearch(String query) {

        musicApi.search(query).enqueue(new Callback<ApiResponse<SearchResponse>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<SearchResponse>> call,
                    Response<ApiResponse<SearchResponse>> response) {

                if (!response.isSuccessful()
                        || response.body() == null
                        || !response.body().isSuccess()
                        || response.body().getData() == null) {
                    return;
                }

                SearchResponse data = response.body().getData();

                if (data.getMedia() != null && !data.getMedia().isEmpty()) {
                    songAdapter.setSongs(data.getMedia());
                    tvSongsHeader.setVisibility(View.VISIBLE);
                    rvSongs.setVisibility(View.VISIBLE);
                } else {
                    tvSongsHeader.setVisibility(View.GONE);
                    rvSongs.setVisibility(View.GONE);
                }

                if (data.getPlaylists() != null && !data.getPlaylists().isEmpty()) {
                    playlistAdapter.setPlaylists(data.getPlaylists());
                    tvPlaylistsHeader.setVisibility(View.VISIBLE);
                    rvPlaylists.setVisibility(View.VISIBLE);
                } else {
                    tvPlaylistsHeader.setVisibility(View.GONE);
                    rvPlaylists.setVisibility(View.GONE);
                }

                if (data.getAlbums() != null && !data.getAlbums().isEmpty()) {
                    albumAdapter.setAlbums(data.getAlbums());
                    tvAlbumsHeader.setVisibility(View.VISIBLE);
                    rvAlbums.setVisibility(View.VISIBLE);
                } else {
                    tvAlbumsHeader.setVisibility(View.GONE);
                    rvAlbums.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<SearchResponse>> call,
                    Throwable t) {

            }
        });
    }

    private void clearResults() {

        songAdapter.setSongs(new ArrayList<>());
        playlistAdapter.setPlaylists(new ArrayList<>());
        albumAdapter.setAlbums(new ArrayList<>());

        tvSongsHeader.setVisibility(View.GONE);
        tvPlaylistsHeader.setVisibility(View.GONE);
        tvAlbumsHeader.setVisibility(View.GONE);

        artistAdapter.setArtists(new ArrayList<>());
        tvArtistsHeader.setVisibility(View.GONE);
        rvArtists.setVisibility(View.GONE);
    }
    private void searchArtists(String query) {
        musicApi.searchArtists(query)
                .enqueue(new Callback<ApiResponse<List<Artist>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Artist>>> call,
                            Response<ApiResponse<List<Artist>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null
                                && !response.body().getData().isEmpty()) {

                            artistAdapter.setArtists(response.body().getData());
                            tvArtistsHeader.setVisibility(View.VISIBLE);
                            rvArtists.setVisibility(View.VISIBLE);

                        } else {
                            tvArtistsHeader.setVisibility(View.GONE);
                            rvArtists.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Artist>>> call,
                            Throwable t) {

                        tvArtistsHeader.setVisibility(View.GONE);
                        rvArtists.setVisibility(View.GONE);
                    }
                });
    }
}
