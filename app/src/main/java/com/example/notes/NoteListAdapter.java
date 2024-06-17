package com.example.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListViewHolder> {
    private final List<NoteCard> noteList;
    private final NoteListListener noteListListener;

    public NoteListAdapter(List<NoteCard> noteList, NoteListListener noteListListener) {
        this.noteList = noteList;
        this.noteListListener = noteListListener;
    }

    public interface NoteListListener {
        void onCountChanged(int count);
        void onItemClicked(NoteCard noteCard);
        void onItemLongClicked(NoteCard noteCard);
        boolean onItemRemove(NoteCard noteCard);
    }

    @NonNull
    @Override
    public NoteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);

        return new NoteListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListViewHolder holder, int position) {
        NoteCard noteCard = noteList.get(position);
        holder.title.setText(noteCard.getTitle());
        holder.summary.setText(noteCard.getSummary());

        if (position == 0 && noteList.size() == 1) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card);
        } else if (position == 0) {
            holder.itemView.setBackgroundResource(R.drawable.bg_card_first);
        } else if (position == noteList.size() - 1) {
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
            noteListListener.onItemClicked(noteList.get(position));
        });

        holder.itemView.setOnLongClickListener(v -> {
            noteListListener.onItemLongClicked(noteList.get(position));
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public boolean removeItem(int position) {
        if (noteListListener.onItemRemove(noteList.get(position))) {
            noteList.remove(position);
            notifyItemRemoved(position);

            if (position == 0) {
                notifyItemChanged(0);
            } else if (position == noteList.size()) {
                notifyItemChanged(position - 1);
            }

            noteListListener.onCountChanged(noteList.size());
            return true;
        }
        return false;
    }
}
