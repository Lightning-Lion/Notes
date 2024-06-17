package com.example.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;

public class NotePanelDialog extends DialogFragment {
    private final NoteCard noteCard;
    private boolean isChanged;
    private final NotePaneDialogListener notePaneDialogListener;
    private Button location;

    public NotePanelDialog(NoteCard noteCard, NotePaneDialogListener notePaneDialogListener) {
        this.noteCard = noteCard;
        if (this.noteCard.getLocation().isEmpty()) {
            this.noteCard.setLocation("杭州电子科技大学信息工程学院");
        }
        this.notePaneDialogListener = notePaneDialogListener;
        isChanged = false;
    }

    public interface NotePaneDialogListener {
        void onItemUpdated(NoteCard noteCard);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        View view = getLayoutInflater().inflate(R.layout.dialog_note_panel, null);
        builder.setView(view);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.gravity = Gravity.BOTTOM;
            windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(windowParams);
        }

        TextView title = view.findViewById(R.id.tv_note_title);
        title.setText(noteCard.getTitle());

        TextView time = view.findViewById(R.id.tv_note_time);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
        time.setText(dateFormat.format(noteCard.getTime()));

        location = view.findViewById(R.id.btn_note_location);
        location.setText(noteCard.getLocation());
        location.setOnClickListener(v -> {
            TextInputDialog textInputDialog = new TextInputDialog(getString(R.string.dialog_location_change), getString(R.string.dialog_location_change_hint), noteCard.getLocation(), new TextInputDialog.TextInputDialogListener() {
                @Override
                public void onInputConfirm(String data) {
                    noteCard.setLocation(data);
                    location.setText(data);
                    isChanged = true;
                }
            });
            textInputDialog.show(requireActivity().getSupportFragmentManager(), "");
        });

        Button folder = view.findViewById(R.id.btn_note_folder);
        folder.setText(noteCard.getFolder().getName());
        folder.setOnClickListener(v -> {
            FolderSelectDialog folderSelectDialog = new FolderSelectDialog(new FolderSelectDialog.FolderSelectDialogListener() {
                @Override
                public void onSelectConfirm(FolderInfo folderInfo) {
                    FolderInfo currentFolderInfo = noteCard.getFolder();
                    currentFolderInfo.setId(folderInfo.getId());
                    currentFolderInfo.setName(folderInfo.getName());
                    folder.setText(folderInfo.getName());
                    isChanged = true;
                }
            });
            folderSelectDialog.show(requireActivity().getSupportFragmentManager(), "");
        });

        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isChanged) {
            notePaneDialogListener.onItemUpdated(noteCard);
        }
    }
}
