package com.example.rhythmixmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.R;
import com.example.rhythmixmobile.model.ShareItem;

import java.util.ArrayList;
import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder> {

    private List<ShareItem> shares = new ArrayList<>();
    private boolean inboxMode = true;

    public void setData(List<ShareItem> shares, boolean inboxMode) {
        this.shares = shares;
        this.inboxMode = inboxMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_share, parent, false);

        return new ShareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareViewHolder holder, int position) {
        ShareItem item = shares.get(position);

        String title = item.getDisplayTitle();
        if (title == null || title.isEmpty()) {
            title = "Không có tiêu đề";
        }

        holder.tvShareTitle.setText(item.getDisplayType() + ": " + title);

        if (inboxMode) {
            holder.tvShareFromTo.setText(item.getSenderName() + " đã chia sẻ với bạn");
        } else {
            holder.tvShareFromTo.setText("Bạn đã chia sẻ với " + item.getReceiverName());
        }

        if (item.getMessage() != null && !item.getMessage().isEmpty()) {
            holder.tvShareMessage.setVisibility(View.VISIBLE);
            holder.tvShareMessage.setText(item.getMessage());
        } else {
            holder.tvShareMessage.setVisibility(View.GONE);
        }

        holder.tvShareTime.setText(item.getSharedAt());
    }

    @Override
    public int getItemCount() {
        return shares == null ? 0 : shares.size();
    }

    static class ShareViewHolder extends RecyclerView.ViewHolder {

        TextView tvShareTitle, tvShareFromTo, tvShareMessage, tvShareTime;

        public ShareViewHolder(@NonNull View itemView) {
            super(itemView);

            tvShareTitle = itemView.findViewById(R.id.tvShareTitle);
            tvShareFromTo = itemView.findViewById(R.id.tvShareFromTo);
            tvShareMessage = itemView.findViewById(R.id.tvShareMessage);
            tvShareTime = itemView.findViewById(R.id.tvShareTime);
        }
    }
}