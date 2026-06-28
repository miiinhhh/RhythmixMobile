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
import com.example.rhythmixmobile.model.Album;
import com.example.rhythmixmobile.utils.Constants;

import java.util.List;

public class SearchAlbumAdapter
        extends RecyclerView.Adapter<SearchAlbumAdapter.ViewHolder> {

    private List<Album> albums;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Album album);
    }

    public SearchAlbumAdapter(List<Album> albums,
                              OnItemClickListener listener) {
        this.albums = albums;
        this.listener = listener;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_search, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Album album = albums.get(position);

        holder.tvTitle.setText(album.getTitle());
        holder.tvArtist.setText(album.getArtistName());

        String image = album.getCoverImageUrl();

        if (image != null && !image.startsWith("http")) {
            image = Constants.BASE_URL + image;
        }

        Glide.with(holder.itemView.getContext())
                .load(image)
                .placeholder(R.drawable.ic_music_note)
                .error(R.drawable.ic_music_note)
                .into(holder.ivThumb);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onItemClick(album);
        });
    }

    @Override
    public int getItemCount() {
        return albums == null ? 0 : albums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivThumb;
        TextView tvTitle, tvArtist;

        ViewHolder(View itemView) {
            super(itemView);

            ivThumb = itemView.findViewById(R.id.ivSongThumb);
            tvTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtist = itemView.findViewById(R.id.tvSongArtist);
        }
    }
}