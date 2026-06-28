package com.example.rhythmixmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.R;
import com.example.rhythmixmobile.model.Playlist;

import java.util.List;

public class SearchPlaylistAdapter extends RecyclerView.Adapter<SearchPlaylistAdapter.ViewHolder> {

    private List<Playlist> playlists;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Playlist playlist);
    }

    public SearchPlaylistAdapter(List<Playlist> playlists, OnItemClickListener listener) {
        this.playlists = playlists;
        this.listener = listener;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.tvTitle.setText(playlist.getName());
        Glide.with(holder.itemView.getContext())
                .load(playlist.getCoverImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivThumb);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(playlist));
    }

    @Override
    public int getItemCount() {
        return playlists != null ? playlists.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivPlaylistThumb);
            tvTitle = itemView.findViewById(R.id.tvPlaylistTitle);
        }
    }
}
