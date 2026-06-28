package com.example.rhythmixmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rhythmixmobile.PlaylistDetailActivity;
import com.example.rhythmixmobile.R;
import com.example.rhythmixmobile.SongDetailActivity;
import com.example.rhythmixmobile.model.Album;
import com.example.rhythmixmobile.model.LibraryItem;
import com.example.rhythmixmobile.model.Playlist;
import com.example.rhythmixmobile.model.Song;
import com.example.rhythmixmobile.utils.Constants;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    private Context context;
    private List<LibraryItem> items;

    public LibraryAdapter(Context context, List<LibraryItem> items) {
        this.context = context;
        this.items = items;
    }

    public void setItems(List<LibraryItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LibraryItem item = items.get(position);

        if (item.getType() == LibraryItem.TYPE_PLAYLIST) {
            Playlist playlist = item.getPlaylist();

            holder.tvTitle.setText(playlist.getName());
            holder.tvSubtitle.setText("Playlist");

            loadImage(holder.ivThumb, playlist.getThumbnailUrl());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PlaylistDetailActivity.class);
                intent.putExtra("playlist", playlist);
                context.startActivity(intent);
            });

        } else if (item.getType() == LibraryItem.TYPE_SONG) {
            Song song = item.getSong();

            holder.tvTitle.setText(song.getTitle());
            holder.tvSubtitle.setText("Song • " + song.getArtist());

            loadImage(holder.ivThumb, song.getThumbnailUrl());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, SongDetailActivity.class);
                intent.putExtra("song", song);
                context.startActivity(intent);
            });

        } else if (item.getType() == LibraryItem.TYPE_ALBUM) {
            Album album = item.getAlbum();

            holder.tvTitle.setText(album.getTitle());
            holder.tvSubtitle.setText("Album • " + album.getTrackCount() + " songs");

            loadImage(holder.ivThumb, album.getCoverImageUrl());

            holder.itemView.setOnClickListener(v -> {
                // Sau này mở AlbumDetailActivity
            });
        }
    }

    private void loadImage(ImageView imageView, String url) {
        if (url != null && !url.isEmpty() && !url.startsWith("http")) {
            url = Constants.BASE_URL + url;
        }

        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_music_note)
                .error(R.drawable.ic_music_note)
                .centerCrop()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle, tvSubtitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}