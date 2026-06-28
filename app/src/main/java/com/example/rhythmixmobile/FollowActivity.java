package com.example.rhythmixmobile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FollowActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        String userId = getIntent().getStringExtra("userId");
        int tab = getIntent().getIntExtra("tab", 0);

        FollowPagerAdapter adapter =
                new FollowPagerAdapter(this, userId);

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(
                tabLayout,
                viewPager,
                (tabItem, position) -> {
                    if (position == 0) {
                        tabItem.setText("Followers");
                    } else {
                        tabItem.setText("Following");
                    }
                }
        ).attach();

        viewPager.setCurrentItem(tab, false);
    }
}