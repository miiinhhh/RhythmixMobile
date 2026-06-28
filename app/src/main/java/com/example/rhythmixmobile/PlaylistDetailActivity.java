package com.example.rhythmixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rhythmixmobile.model.MediaDiscoveryResponse;
import com.example.rhythmixmobile.model.MediaItem;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.adapter.SearchSongAdapter;
import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.AddTrackRequest;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.Playlist;
import com.example.rhythmixmobile.model.PlaylistDetailResponse;
import com.example.rhythmixmobile.model.PlaylistTrack;
import com.example.rhythmixmobile.model.Song;
import com.example.rhythmixmobile.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistDetailActivity extends AppCompatActivity {

    private ImageView ivThumb;
    private TextView tvTitle, tvDescription;
    private RecyclerView rvSongs;
    private FloatingActionButton btnAddSong;
    private SearchSongAdapter adapter;
    private MusicApi musicApi;
    private Playlist playlist;
    private List<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        playlist = (Playlist) getIntent().getSerializableExtra("playlist");
        if (playlist == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        displayPlaylistInfo();
        setupRecyclerView();

        musicApi = RetrofitClient
                .getInstance(this)
                .create(MusicApi.class);
        
        loadSongs();

        btnAddSong.setOnClickListener(v -> showAddSongDialog());
    }

    private void initViews() {
        ivThumb = findViewById(R.id.ivPlaylistThumb);
        tvTitle = findViewById(R.id.tvPlaylistTitle);
        tvDescription = findViewById(R.id.tvPlaylistDescription);
        rvSongs = findViewById(R.id.rvPlaylistSongs);
        btnAddSong = findViewById(R.id.btnAddSong);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent intent = new Intent(this, ShareActivity.class);
            intent.putExtra("playlistId", playlist.getPlaylistId());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayPlaylistInfo() {
        tvTitle.setText(playlist.getName());

        tvDescription.setText(
                playlist.getDescription() != null && !playlist.getDescription().isEmpty()
                        ? playlist.getDescription()
                        : "Public playlist"
        );

        String imageUrl = playlist.getThumbnailUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {

            if (!imageUrl.startsWith("http")) {
                imageUrl = Constants.BASE_URL + imageUrl;
            }

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .centerCrop()
                    .into(ivThumb);

        } else {
            ivThumb.setImageResource(R.drawable.ic_music_note);
        }
    }

    private void setupRecyclerView() {
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchSongAdapter(songList, song -> {
            Intent intent = new Intent(this, SongDetailActivity.class);
            intent.putExtra("song", song);
            startActivity(intent);
        });

        adapter.setOnItemLongClickListener(this::showDeleteConfirmation);

        rvSongs.setAdapter(adapter);
    }

    private void showDeleteConfirmation(Song song) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bài hát")
                .setMessage("Bạn có muốn xóa bài hát '" + song.getTitle() + "' khỏi playlist này không?")
                .setPositiveButton("Xóa", (dialog, which) -> removeSong(song))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void removeSong(Song song) {
        musicApi.removeSongFromPlaylist(playlist.getPlaylistId(), song.getMediaId()).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PlaylistDetailActivity.this, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
                    loadSongs();
                } else {
                    Toast.makeText(PlaylistDetailActivity.this, "Không thể xóa bài hát", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(PlaylistDetailActivity.this, "Lỗi khi xóa bài hát", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSongs() {

        musicApi.getPlaylistDetail(playlist.getPlaylistId())
                .enqueue(new Callback<PlaylistDetailResponse>() {

                    @Override
                    public void onResponse(
                            Call<PlaylistDetailResponse> call,
                            Response<PlaylistDetailResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            songList.clear();

                            for (PlaylistTrack track : response.body().getData().getTracks()) {

                                Song song = new Song(
                                        track.getMediaId(),
                                        track.getTitle(),
                                        track.getArtistName(),
                                        Constants.BASE_URL + track.getThumbnailUrl(),
                                        "",
                                        Constants.BASE_URL + track.getFilePath()
                                );

                                songList.add(song);
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<PlaylistDetailResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                PlaylistDetailActivity.this,
                                "Lỗi tải playlist",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showAddSongDialog() {

        musicApi.getDiscoveryMedia(1, 100)
                .enqueue(new Callback<MediaDiscoveryResponse>() {

                    @Override
                    public void onResponse(
                            Call<MediaDiscoveryResponse> call,
                            Response<MediaDiscoveryResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            List<Song> songs = new ArrayList<>();

                            for (MediaItem item : response.body().getData()) {

                                Song song = new Song(
                                        item.getMediaId(),
                                        item.getTitle(),
                                        item.getArtistName(),
                                        Constants.BASE_URL + item.getThumbnailUrl(),
                                        item.getDescription(),
                                        Constants.BASE_URL + item.getFilePath()
                                );

                                songs.add(song);
                            }

                            if (songs.isEmpty()) {
                                Toast.makeText(
                                        PlaylistDetailActivity.this,
                                        "Không có bài hát",
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            showSelectionDialog(songs);

                        } else {

                            Toast.makeText(
                                    PlaylistDetailActivity.this,
                                    "Không tải được danh sách bài hát",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<MediaDiscoveryResponse> call,
                            Throwable t) {

                        Toast.makeText(
                                PlaylistDetailActivity.this,
                                "Lỗi: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showSelectionDialog(List<Song> allSongs) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_add_song, null);

        RecyclerView rvAddSongs = view.findViewById(R.id.rvAddSongs);
        rvAddSongs.setLayoutManager(new LinearLayoutManager(this));

        SearchSongAdapter addAdapter = new SearchSongAdapter(allSongs, song -> {
            addSongToPlaylist(song);
            dialog.dismiss();
        });

        rvAddSongs.setAdapter(addAdapter);

        dialog.setContentView(view);
        dialog.show();
    }

    private void addSongToPlaylist(Song song) {
        AddTrackRequest request = new AddTrackRequest(song.getMediaId(), 0);

        musicApi.addSongToPlaylist(playlist.getPlaylistId(), request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PlaylistDetailActivity.this, "Đã thêm " + song.getTitle(), Toast.LENGTH_SHORT).show();
                    loadSongs();
                } else {
                    Toast.makeText(PlaylistDetailActivity.this, "Không thể thêm bài hát", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(PlaylistDetailActivity.this, "Lỗi khi thêm bài hát", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
