package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FolderListActivity extends AppCompatActivity {
    private Database database;
    private List<FolderInfo> folderList;
    private FolderListAdapter folderListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_folder_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = new Database(this);
        findViewById(R.id.btn_folder_new).setOnClickListener(v -> {
            TextInputDialog dialog = new TextInputDialog(getString(R.string.dialog_folder_new), getString(R.string.dialog_folder_new_hint), null, new TextInputDialog.TextInputDialogListener() {
                @Override
                public void onInputConfirm(String data) {
                    String newId = database.insertFolder(data);
                    if (!Objects.equals(newId, "-1")) {
                        folderList.add(new FolderInfo(newId, data));
                        folderListAdapter.notifyItemInserted(folderList.size() - 1);
                        if (folderList.size() > 1) {
                            folderListAdapter.notifyItemChanged(folderList.size() - 2);
                        }
                    }
                }
            });
            dialog.show(getSupportFragmentManager(), "");
        });
        findViewById(R.id.btn_note_new).setVisibility(View.GONE);

        RecyclerView recyclerView = findViewById(R.id.rv_folder_list_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        folderList = database.getFolderList();
        folderListAdapter = new FolderListAdapter(folderList, new FolderListAdapter.FolderListListener() {
            @Override
            public void onItemClicked(FolderInfo folderInfo) {
                Intent intent = new Intent(FolderListActivity.this, NoteListActivity.class);
                intent.putExtra("folderInfo", folderInfo);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(FolderInfo folderInfo) {
                TextInputDialog dialog = new TextInputDialog(getString(R.string.dialog_folder_rename), getString(R.string.dialog_folder_rename_hint), folderInfo.getName(), new TextInputDialog.TextInputDialogListener() {
                    @Override
                    public void onInputConfirm(String data) {
                        if (database.updateFolder(folderInfo.getId(), data)) {
                            for (int i = 0; i < folderList.size(); i++) {
                                FolderInfo currentFolderInfo = folderList.get(i);
                                if (currentFolderInfo.getId().equals(folderInfo.getId())) {
                                    currentFolderInfo.setName(data);
                                    folderListAdapter.notifyItemChanged(i);

                                    break;
                                }
                            }
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
            }

            @Override
            public boolean onItemRemove(FolderInfo folderInfo) {
                return database.deleteFolder(folderInfo.getId());
            }
        });
        recyclerView.setAdapter(folderListAdapter);

        FolderListSwipe folderListSwipe = new FolderListSwipe(folderListAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(folderListSwipe);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}