package com.example.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderSelectDialog extends DialogFragment  {
    private final FolderSelectDialogListener folderSelectDialogListener;

    public FolderSelectDialog(FolderSelectDialogListener folderSelectDialogListener) {
        this.folderSelectDialogListener = folderSelectDialogListener;
    }

    public interface FolderSelectDialogListener {
        void onSelectConfirm(FolderInfo folderInfo);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        View view = getLayoutInflater().inflate(R.layout.dialog_folder_select, null);
        builder.setView(view);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Rect displayRectangle = new Rect();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            window.setLayout(displayRectangle.width(), displayRectangle.height());

            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.gravity = Gravity.CENTER_VERTICAL;

            window.setAttributes(windowParams);
        }

        RecyclerView recyclerView = view.findViewById(R.id.rv_dialog_folder_select_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(dialog.getContext()));

        Database database = new Database(dialog.getContext());
        FolderSelectListAdapter folderSelectListAdapter = new FolderSelectListAdapter(database.getFolderList(), new FolderSelectListAdapter.FolderSelectListListener() {
            @Override
            public void onItemClicked(FolderInfo folderInfo) {
                folderSelectDialogListener.onSelectConfirm(folderInfo);
                dismiss();
            }
        });

        recyclerView.setAdapter(folderSelectListAdapter);

        view.findViewById(R.id.btn_dialog_folder_select_cancel).setOnClickListener(v -> dismiss());

        return dialog;
    }
}
