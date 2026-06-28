package com.example.rhythmixmobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FollowPagerAdapter extends FragmentStateAdapter {

    private final String userId;

    public FollowPagerAdapter(FragmentActivity activity, String userId) {
        super(activity);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);

        Fragment fragment;

        if (position == 0) {
            fragment = new FollowersFragment();
        } else {
            fragment = new FollowingFragment();
        }

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}