package com.example.rhythmixmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.R;
import com.example.rhythmixmobile.model.FollowUser;
import com.example.rhythmixmobile.utils.Constants;

import java.util.List;

public class FollowUserAdapter extends RecyclerView.Adapter<FollowUserAdapter.FollowUserViewHolder> {

    private final Context context;
    private final List<FollowUser> users;

    public FollowUserAdapter(Context context, List<FollowUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public FollowUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_follow_user, parent, false);
        return new FollowUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowUserViewHolder holder, int position) {
        FollowUser user = users.get(position);

        holder.txtUserName.setText(
                user.displayName != null && !user.displayName.isEmpty()
                        ? user.displayName
                        : user.userName
        );

        holder.txtUserBio.setText(
                user.bio != null && !user.bio.isEmpty()
                        ? user.bio
                        : "Music lover"
        );

        if (user.avatarUrl != null && !user.avatarUrl.isEmpty()) {
            String imageUrl = Constants.BASE_URL + user.avatarUrl;

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .circleCrop()
                    .into(holder.imgUserAvatar);
        } else {
            holder.imgUserAvatar.setImageResource(R.drawable.ic_user);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class FollowUserViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUserAvatar;
        TextView txtUserName, txtUserBio;

        public FollowUserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserBio = itemView.findViewById(R.id.txtUserBio);
        }
    }
}