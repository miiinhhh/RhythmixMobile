package com.example.rhythmixmobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.api.PlaylistApi;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.CreatePlaylistRequest;

import com.example.rhythmixmobile.adapter.SearchPlaylistAdapter;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.Playlist;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPlaylistsActivity extends AppCompatActivity {

    private RecyclerView rvMyPlaylists;
    private SearchPlaylistAdapter adapter;
    private PlaylistApi playlistApi;
    private List<Playlist> playlistList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_playlists);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        rvMyPlaylists = findViewById(R.id.rvMyPlaylists);
        rvMyPlaylists.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new SearchPlaylistAdapter(playlistList, playlist -> {
            Toast.makeText(this, "Mở playlist: " + playlist.getName(), Toast.LENGTH_SHORT).show();
        });
        rvMyPlaylists.setAdapter(adapter);

        playlistApi = RetrofitClient
                .getInstance(this)
                .create(PlaylistApi.class);

        FloatingActionButton fabAdd = findViewById(R.id.fabAddPlaylist);
        fabAdd.setOnClickListener(v -> showCreatePlaylistDialog());

        loadMyPlaylists();
    }

    private void loadMyPlaylists() {
        playlistApi.getMyPlaylists().enqueue(new Callback<ApiResponse<List<Playlist>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<Playlist>>> call,
                    Response<ApiResponse<List<Playlist>>> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {

                    playlistList.clear();
                    playlistList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(MyPlaylistsActivity.this, "Không tải được playlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Playlist>>> call, Throwable t) {
                Toast.makeText(MyPlaylistsActivity.this, "Lỗi tải playlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tạo Playlist mới");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_playlist, null);
        EditText etName = view.findViewById(R.id.etPlaylistName);
        builder.setView(view);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                createNewPlaylist(name);
            } else {
                Toast.makeText(this, "Vui lòng nhập tên playlist", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void createNewPlaylist(String name) {
        CreatePlaylistRequest request =
                new CreatePlaylistRequest(name, "", true);

        playlistApi.createPlaylist(request).enqueue(new Callback<ApiResponse<Playlist>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<Playlist>> call,
                    Response<ApiResponse<Playlist>> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    Toast.makeText(MyPlaylistsActivity.this, "Đã tạo playlist", Toast.LENGTH_SHORT).show();
                    loadMyPlaylists();

                } else {
                    Toast.makeText(MyPlaylistsActivity.this, "Tạo playlist thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Playlist>> call, Throwable t) {
                Toast.makeText(MyPlaylistsActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
