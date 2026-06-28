package com.example.rhythmixmobile.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.R;
import com.example.rhythmixmobile.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.tvContent.setText(formatNotification(notification));
        holder.tvTime.setText(notification.getCreatedAt());

        if (!notification.isRead()) {
            holder.tvContent.setTypeface(null, Typeface.BOLD);
            holder.dotUnread.setVisibility(View.VISIBLE);
        } else {
            holder.tvContent.setTypeface(null, Typeface.NORMAL);
            holder.dotUnread.setVisibility(View.GONE);
        }

        String type = notification.getType();

        if (type != null && type.toLowerCase().contains("follow")) {
            holder.tvIcon.setText("👤");
        } else if (type != null && type.toLowerCase().contains("playlist")) {
            holder.tvIcon.setText("🎧");
        } else if (type != null && type.toLowerCase().contains("song")) {
            holder.tvIcon.setText("🎵");
        } else {
            holder.tvIcon.setText("🔔");
        }
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView tvIcon, tvContent, tvTime;
        View dotUnread;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIcon = itemView.findViewById(R.id.tvNotificationIcon);
            tvContent = itemView.findViewById(R.id.tvNotificationContent);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            dotUnread = itemView.findViewById(R.id.dotUnread);
        }
    }
    private String formatNotification(Notification notification) {
        String type = notification.getType();
        String content = notification.getContent();

        if (type == null) return content;

        String lowerType = type.toLowerCase();

        if (lowerType.contains("follow")) {
            return "Có người đã follow bạn";
        }

        if (lowerType.contains("playlist")) {
            return "Có người đã chia sẻ playlist với bạn";
        }

        if (lowerType.contains("song") || lowerType.contains("media")) {
            return "Có người đã chia sẻ bài hát với bạn";
        }

        return content != null ? content : "Bạn có thông báo mới";
    }
}