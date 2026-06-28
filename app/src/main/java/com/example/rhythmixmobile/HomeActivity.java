package com.example.rhythmixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView btnProfile = findViewById(R.id.btnInbox);

        btnProfile.setOnClickListener(v -> {
            Intent intent =
                    new Intent(HomeActivity.this,
                            ProfileActivity.class);

            startActivity(intent);

            overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
            );
        });
    }
}