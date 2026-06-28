package com.example.rhythmixmobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rhythmixmobile.api.RetrofitClient;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.example.rhythmixmobile.api.PlaylistApi;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.CreatePlaylistRequest;
import com.example.rhythmixmobile.model.Playlist;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePlaylistActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private SwitchMaterial switchPublic;
    private Button btnCreate;
    private PlaylistApi playlistApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        switchPublic = findViewById(R.id.switchPublic);
        btnCreate = findViewById(R.id.btnCreate);

        playlistApi = RetrofitClient
                .getInstance(this)
                .create(PlaylistApi.class);

        btnCreate.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            boolean isPublic = switchPublic.isChecked();

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên playlist", Toast.LENGTH_SHORT).show();
                return;
            }

            createPlaylist(title, description, isPublic);
        });
    }

    private void createPlaylist(String title, String description, boolean isPublic) {
        CreatePlaylistRequest request =
                new CreatePlaylistRequest(title, description, isPublic);

        playlistApi.createPlaylist(request).enqueue(new Callback<ApiResponse<Playlist>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<Playlist>> call,
                    Response<ApiResponse<Playlist>> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    Toast.makeText(CreatePlaylistActivity.this, "Tạo playlist thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();

                } else {
                    Toast.makeText(CreatePlaylistActivity.this, "Tạo playlist thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Playlist>> call, Throwable t) {
                Toast.makeText(CreatePlaylistActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
