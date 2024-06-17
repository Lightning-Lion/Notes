package com.example.notes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.Serializable;

public class TextInputDialog extends DialogFragment  {
    private final String title;
    private final String hint;
    private final String data;
    private final TextInputDialogListener textInputDialogListener;

    public TextInputDialog(String title, String hint, String data, TextInputDialogListener textInputDialogListener) {
        this.title = title;
        this.hint = hint;
        this.data = data;
        this.textInputDialogListener = textInputDialogListener;
    }

    public interface TextInputDialogListener {
        void onInputConfirm(String data);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        View view = getLayoutInflater().inflate(R.layout.dialog_text_input, null);
        builder.setView(view);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.gravity = Gravity.CENTER_VERTICAL;
            windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(windowParams);
        }

        TextView titleView = view.findViewById(R.id.tv_dialog_text_input_title);
        titleView.setText(title);
        EditText dataInput = view.findViewById(R.id.et_dialog_text_input_input);
        dataInput.setHint(hint);
        if (data != null) {
            dataInput.setText(data);
        }

        view.findViewById(R.id.btn_dialog_text_input_cancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btn_dialog_text_input_confirm).setOnClickListener(v -> {
            String newData = String.valueOf(dataInput.getText());
            if (!newData.equals(data)) {
                textInputDialogListener.onInputConfirm(newData);
            }
            dismiss();
        });

        return dialog;
    }
}
