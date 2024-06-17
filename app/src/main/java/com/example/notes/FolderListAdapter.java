package com.example.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListViewHolder> {
    private final List<FolderInfo> folderList;
    private final FolderListListener folderListListener;

    public FolderListAdapter(List<FolderInfo> folderList, FolderListListener folderListListener) {
        this.folderList = folderList;
        this.folderListListener = folderListListener;
    }

    public interface FolderListListener {
        void onItemClicked(FolderInfo folderInfo);
        void onItemLongClicked(FolderInfo folderInfo);
        boolean onItemRemove(FolderInfo folderInfo);
    }

    @NonNull
    @Override
    public FolderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);

        return new FolderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderListViewHolder holder, int position) {
        FolderInfo folderInfo = folderList.get(position);
        holder.name.setText(folderInfo.getName());

        if (position == 0 && folderList.size() == 1) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card);
        } else if (position == 0) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card_first);
        } else if (position == folderList.size() - 1) {
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
            folderListListener.onItemClicked(folderList.get(position));
        });

        holder.itemView.setOnLongClickListener(v -> {
            folderListListener.onItemLongClicked(folderList.get(position));
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return folderList.size();
    }

    public boolean removeItem(int position) {
        if (folderListListener.onItemRemove(folderList.get(position))) {
            folderList.remove(position);
            notifyItemRemoved(position);

            if (position == 0) {
                notifyItemChanged(0);
            } else if (position == folderList.size()) {
                notifyItemChanged(position - 1);
            }

            return true;
        }
        return false;
    }
}
