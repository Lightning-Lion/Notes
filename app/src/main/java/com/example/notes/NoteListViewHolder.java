package com.example.notes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteListViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView summary;

    public NoteListViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.tv_note_title);
        summary = itemView.findViewById(R.id.tv_note_summary);
    }
}
