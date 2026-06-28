package com.example.rhythmixmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.rhythmixmobile.utils.Constants;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.R;
import com.example.rhythmixmobile.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlistList;
    private OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }
    public void setPlaylists(List<Playlist> playlists) {
        this.playlistList = playlists;
        notifyDataSetChanged();
    }

    public PlaylistAdapter(List<Playlist> playlistList, OnPlaylistClickListener listener) {
        this.playlistList = playlistList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_horizontal, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);

        holder.tvTitle.setText(playlist.getName());

        String imageUrl = playlist.getThumbnailUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {

            if (!imageUrl.startsWith("http")) {
                imageUrl = Constants.BASE_URL + imageUrl;
            }

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .centerCrop()
                    .into(holder.ivThumb);

        } else {
            holder.ivThumb.setImageResource(R.drawable.ic_music_note);
        }

        holder.itemView.setOnClickListener(v -> listener.onPlaylistClick(playlist));
    }

    @Override
    public int getItemCount() {
        return playlistList != null ? playlistList.size() : 0;
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivPlaylistThumb);
            tvTitle = itemView.findViewById(R.id.tvPlaylistTitle);
        }
    }
}
