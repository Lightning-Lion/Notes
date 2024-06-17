package com.example.notes;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class NoteListSwipe extends ItemTouchHelper.SimpleCallback {

    private final NoteListAdapter adapter;

    public NoteListSwipe(NoteListAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            int position = viewHolder.getAdapterPosition();
            if (!adapter.removeItem(position)) {
                adapter.notifyItemChanged(position);
            }
        }
    }
}
