package com.example.rhythmixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rhythmixmobile.api.InteractionApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.model.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SongDetailActivity extends AppCompatActivity {

    private ImageView ivSongThumb;
    private InteractionApi interactionApi;
    private Song currentSong;
    private TextView tvSongTitle, tvSongArtist, tvSongDescription;
    private FloatingActionButton btnPlay;
    private ImageButton btnBack, btnFavorite, btnShare;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        initViews();

        interactionApi = RetrofitClient
                .getInstance(this)
                .create(InteractionApi.class);

        currentSong = (Song) getIntent().getSerializableExtra("song");

        displaySongInfo();
        loadFavoriteState();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivSongThumb = findViewById(R.id.ivSongThumb);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        tvSongArtist = findViewById(R.id.tvSongArtist);
        tvSongDescription = findViewById(R.id.tvSongDescription);
        btnPlay = findViewById(R.id.btnPlay);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnShare = findViewById(R.id.btnShare);
    }

    private void displaySongInfo() {
        Song song = currentSong;
        if (song != null) {
            tvSongTitle.setText(song.getTitle());
            tvSongArtist.setText(song.getArtist());
            tvSongDescription.setText(song.getDescription() != null ? song.getDescription() : "Không có mô tả cho bài hát này.");

            Glide.with(this)
                    .load(song.getThumbnailUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(ivSongThumb);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlay.setOnClickListener(v -> {
            Song song = (Song) getIntent().getSerializableExtra("song");
            if (song != null) {
                Intent intent = new Intent(this, NowPlayingActivity.class);
                intent.putExtra("song", song);
                startActivity(intent);
            }
        });

        btnFavorite.setOnClickListener(v -> {

            if(currentSong == null) return;

            interactionApi.toggleFavorite(currentSong.getMediaId())
                    .enqueue(new Callback<ApiResponse<Object>>() {

                        @Override
                        public void onResponse(
                                Call<ApiResponse<Object>> call,
                                Response<ApiResponse<Object>> response) {

                            if(response.isSuccessful()
                                    && response.body()!=null
                                    && response.body().isSuccess()){

                                isFavorite = !isFavorite;

                                updateFavoriteIcon();

                                Toast.makeText(
                                        SongDetailActivity.this,
                                        isFavorite
                                                ? "Đã thêm vào yêu thích"
                                                : "Đã bỏ khỏi yêu thích",
                                        Toast.LENGTH_SHORT
                                ).show();

                            }else{

                                Toast.makeText(
                                        SongDetailActivity.this,
                                        "Không thể cập nhật yêu thích",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        }

                        @Override
                        public void onFailure(
                                Call<ApiResponse<Object>> call,
                                Throwable t) {

                            Toast.makeText(
                                    SongDetailActivity.this,
                                    t.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });

        });

        btnShare.setOnClickListener(v -> {

            if (currentSong == null) return;

            ShareBottomSheet
                    .newInstance(currentSong.getMediaId())
                    .show(getSupportFragmentManager(), "share");

        });
    }
    private void loadFavoriteState() {

        if (currentSong == null) return;

        interactionApi.getFavoriteIds()
                .enqueue(new Callback<ApiResponse<List<String>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<String>>> call,
                            Response<ApiResponse<List<String>>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null) {

                            isFavorite =
                                    response.body()
                                            .getData()
                                            .contains(currentSong.getMediaId());

                            updateFavoriteIcon();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<String>>> call,
                            Throwable t) {

                    }
                });
    }
    private void updateFavoriteIcon() {
        btnFavorite.setImageResource(
                isFavorite
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );

        btnFavorite.setColorFilter(
                isFavorite
                        ? android.graphics.Color.parseColor("#1DB954")
                        : android.graphics.Color.parseColor("#B3B3B3")
        );
    }
}
