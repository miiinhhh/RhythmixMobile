package com.example.rhythmixmobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.model.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class NowPlayingActivity extends AppCompatActivity {

    private ImageView ivThumb;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private ImageButton btnBack, btnPrevious, btnNext, btnRepeat, btnShuffle;
    private FloatingActionButton btnPlayPause;

    private ExoPlayer player;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isRepeat = false;
    private boolean isShuffle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        initViews();
        setupPlayer();
        loadSong();
        setupListeners();
    }

    private void initViews() {
        ivThumb = findViewById(R.id.ivPlayerThumb);
        tvTitle = findViewById(R.id.tvPlayerTitle);
        tvArtist = findViewById(R.id.tvPlayerArtist);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        seekBar = findViewById(R.id.playerSeekBar);
        btnBack = findViewById(R.id.btnBack);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnRepeat = findViewById(R.id.btnRepeat);
        btnShuffle = findViewById(R.id.btnShuffle);
        btnPlayPause = findViewById(R.id.btnPlayPause);
    }

    private void setupPlayer() {
        player = new ExoPlayer.Builder(this).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    tvTotalTime.setText(formatTime(player.getDuration()));
                    seekBar.setMax((int) player.getDuration());
                    updateProgress();
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });
    }

    private void loadSong() {
        Song song = (Song) getIntent().getSerializableExtra("song");
        if (song != null) {
            tvTitle.setText(song.getTitle());
            tvArtist.setText(song.getArtist());
            Glide.with(this).load(song.getThumbnailUrl()).into(ivThumb);

            if (song.getStreamUrl() != null) {
                MediaItem mediaItem = MediaItem.fromUri(song.getStreamUrl());
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();
            }
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnPlayPause.setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.play();
            }
        });

        btnRepeat.setOnClickListener(v -> {
            isRepeat = !isRepeat;
            player.setRepeatMode(isRepeat ? Player.REPEAT_MODE_ONE : Player.REPEAT_MODE_OFF);
            btnRepeat.setColorFilter(isRepeat ? ContextCompat.getColor(this, R.color.black) : 0xFFCCCCCC);
        });

        btnShuffle.setOnClickListener(v -> {
            isShuffle = !isShuffle;
            player.setShuffleModeEnabled(isShuffle);
            btnShuffle.setColorFilter(isShuffle ? ContextCompat.getColor(this, R.color.black) : 0xFFCCCCCC);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Next/Prev (Placeholder logic for single song)
        btnNext.setOnClickListener(v -> player.seekToNext());
        btnPrevious.setOnClickListener(v -> player.seekToPrevious());
    }

    private void updateProgress() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    seekBar.setProgress((int) player.getCurrentPosition());
                    tvCurrentTime.setText(formatTime(player.getCurrentPosition()));
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) (millis / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}
