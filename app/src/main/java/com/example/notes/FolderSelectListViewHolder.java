package com.example.notes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FolderSelectListViewHolder extends RecyclerView.ViewHolder {
    public TextView name;

    public FolderSelectListViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.tv_folder_select_name);
    }
}
