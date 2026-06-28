package com.example.rhythmixmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.R;

import com.example.rhythmixmobile.model.UserProfile;
import com.example.rhythmixmobile.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ShareUserAdapter extends RecyclerView.Adapter<ShareUserAdapter.ViewHolder> {

    public interface OnUserSelectedListener {
        void onUserSelected(UserProfile user);
    }

    private final List<UserProfile> users = new ArrayList<>();
    private final List<UserProfile> allUsers = new ArrayList<>();

    private final OnUserSelectedListener listener;

    private int selectedPosition = -1;

    public ShareUserAdapter(OnUserSelectedListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<UserProfile> list) {
        users.clear();
        allUsers.clear();

        users.addAll(list);
        allUsers.addAll(list);

        notifyDataSetChanged();
    }

    public void filter(String keyword) {

        users.clear();

        if (keyword == null || keyword.trim().isEmpty()) {

            users.addAll(allUsers);

        } else {

            keyword = keyword.toLowerCase();

            for (UserProfile user : allUsers) {

                String name =
                        user.displayName != null && !user.displayName.isEmpty()
                                ? user.displayName
                                : user.userName;

                if (name.toLowerCase().contains(keyword)) {
                    users.add(user);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_share_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        UserProfile user = users.get(position);

        holder.tvUsername.setText(
                user.displayName != null && !user.displayName.isEmpty()
                        ? user.displayName
                        : user.userName
        );

        String avatar = user.avatarUrl;

        if (avatar != null
                && !avatar.isEmpty()
                && !avatar.startsWith("http")) {

            avatar = Constants.BASE_URL + avatar;
        }

        Glide.with(holder.itemView.getContext())
                .load(avatar)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .centerCrop()
                .into(holder.ivAvatar);

        holder.rbSelected.setChecked(position == selectedPosition);
        holder.rbSelected.setClickable(false);

        holder.itemView.setOnClickListener(v -> {

            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            int old = selectedPosition;
            selectedPosition = adapterPosition;

            if (old != -1) {
                notifyItemChanged(old);
            }

            notifyItemChanged(selectedPosition);

            listener.onUserSelected(users.get(adapterPosition));
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        TextView tvUsername;
        RadioButton rbSelected;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            rbSelected = itemView.findViewById(R.id.rbSelected);
        }
    }
}