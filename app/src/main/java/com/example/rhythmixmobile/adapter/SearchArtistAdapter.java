package com.example.rhythmixmobile.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.R;
import com.example.rhythmixmobile.model.Artist;
import com.example.rhythmixmobile.utils.Constants;

import java.util.List;

public class SearchArtistAdapter
        extends RecyclerView.Adapter<SearchArtistAdapter.ViewHolder> {

    private List<Artist> artists;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Artist artist);
    }

    public SearchArtistAdapter(List<Artist> artists, OnItemClickListener listener) {
        this.artists = artists;
        this.listener = listener;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_search, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        Artist artist = artists.get(position);

        holder.tvTitle.setText(artist.getName());
        holder.tvTitle.setTextColor(Color.WHITE);

        holder.tvSubtitle.setText(
                artist.getTrackCount() + " songs • "
                        + artist.getFollowerCount() + " followers"
        );
        holder.tvSubtitle.setTextColor(Color.GRAY);

        String imageUrl = artist.getAvatarUrl();

        if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = Constants.BASE_URL + imageUrl;
        }

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .centerCrop()
                .into(holder.ivThumb);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(artist);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists == null ? 0 : artists.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivThumb;
        TextView tvTitle, tvSubtitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivThumb = itemView.findViewById(R.id.ivSongThumb);
            tvTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSongArtist);
        }
    }
}