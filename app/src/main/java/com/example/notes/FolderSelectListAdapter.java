package com.example.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderSelectListAdapter extends RecyclerView.Adapter<FolderSelectListViewHolder> {
    private final List<FolderInfo> folderSelectList;
    private final FolderSelectListListener folderSelectListListener;

    public FolderSelectListAdapter(List<FolderInfo> folderSelectList, FolderSelectListListener folderSelectListListener) {
        this.folderSelectList = folderSelectList;
        this.folderSelectListListener = folderSelectListListener;
    }

    public interface FolderSelectListListener {
        void onItemClicked(FolderInfo folderInfo);
    }

    @NonNull
    @Override
    public FolderSelectListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_select, parent, false);

        return new FolderSelectListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderSelectListViewHolder holder, int position) {
        FolderInfo folderInfo = folderSelectList.get(position);
        holder.name.setText(folderInfo.getName());

        if (position == 0 && folderSelectList.size() == 1) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card);
        } else if (position == 0) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card_first);
        } else if (position == folderSelectList.size() - 1) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card_last);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_card_no_corner);
        }

        if (position > 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
            layoutParams.topMargin = Utils.dpToPx(1);
            holder.itemView.setLayoutParams(layoutParams);
        }

        holder.itemView.setOnClickListener(v -> {
            folderSelectListListener.onItemClicked(folderSelectList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return folderSelectList.size();
    }
}
