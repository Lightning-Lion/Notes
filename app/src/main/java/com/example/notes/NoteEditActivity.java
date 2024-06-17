package com.example.notes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class NoteEditActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_CAMERA = 1001;
    private static final int REQUEST_CODE_READ_MEDIA_IMAGES = 1002;
    private boolean isPageLoaded = false;
    private NoteDetail noteDetail;

    EditText multiLineText = null;
    EditText titleInput = null;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_edit);
        multiLineText = findViewById(R.id.textEditor);
        titleInput = findViewById(R.id.textField);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noteDetail = (NoteDetail) getIntent().getSerializableExtra("noteDetail");

        titleInput.setText(noteDetail != null ? noteDetail.getTitle() : "");
        multiLineText.setText(noteDetail != null ? noteDetail.getContent() : "");
        isPageLoaded = true;

        titleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 在文本变化之前执行的操作
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 在文本变化时执行的操作
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 在文本变化之后执行的操作
                autoSave();

            }
        });

        multiLineText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 在文本变化之前执行的操作
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 在文本变化时执行的操作
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 在文本变化之后执行的操作
                autoSave();

            }
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());



    }

    void autoSave() {
        if (isPageLoaded) {
            String title = titleInput.getText().toString();
            String summary = "";
            String content = multiLineText.getText().toString();

            if ((!title.equals(noteDetail.getTitle()) || !content.equals(noteDetail.getContent())) && (!title.isEmpty() || !content.isEmpty())) {
                noteDetail.setTitle(title);
                noteDetail.setSummary(summary);
                noteDetail.setContent(content);
                Intent intent = new Intent(getPackageName() + ".NOTE_UPDATED");
                intent.putExtra("noteDetail", noteDetail);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}