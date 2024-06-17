package com.example.notes;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NoteListActivity extends AppCompatActivity {
    private TextView footerText;
    private Database database;
    private FolderInfo folderInfo;
    private List<NoteCard> noteList;
    private NoteListAdapter noteListAdapter;
    private SearchFilter searchFilter;
    private final BroadcastReceiver noteUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NoteDetail noteDetail = (NoteDetail) intent.getSerializableExtra("noteDetail");
            if (noteDetail == null) {
                return;
            }

            if (noteDetail.getId() == null) {
                String id = database.insertNote(noteDetail.getFolderId(), noteDetail.getTitle(), noteDetail.getSummary(), noteDetail.getContent());
                if (!Objects.equals(id, "-1")) {
                    noteList.add(0, database.getNoteCard(id));
                    noteListAdapter.notifyItemInserted(0);
                    if (noteList.size() > 1) {
                        noteListAdapter.notifyItemChanged(1);
                    }
                }
            } else {
                if (database.updateNoteDetail(noteDetail.getId(), noteDetail.getTitle(), noteDetail.getSummary(), noteDetail.getContent())) {
                    for (int i = 0; i < noteList.size(); i++) {
                        NoteCard currentNoteCard = noteList.get(i);
                        if (currentNoteCard.getId().equals(noteDetail.getId())) {
                            NoteCard noteCard = database.getNoteCard(noteDetail.getId());
                            noteList.remove(i);
                            noteList.add(0, noteCard);

                            noteListAdapter.notifyItemMoved(i, 0);
                            noteListAdapter.notifyItemChanged(0);
                            if (noteList.size() > 1) {
                                noteListAdapter.notifyItemChanged(1);
                            }

                            break;
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_folder_new).setVisibility(View.GONE);
        findViewById(R.id.btn_note_new).setOnClickListener(v -> {
            NoteDetail emptyNote = new NoteDetail(null, folderInfo.getId(), "", "点击查看详情", "");
            String id = database.insertNote(emptyNote.getFolderId(), emptyNote.getTitle(), emptyNote.getSummary(), emptyNote.getContent());
            if (!Objects.equals(id, "-1")) {
                emptyNote.setId(id);
                noteList.add(0, database.getNoteCard(id));
                noteListAdapter.notifyItemInserted(0);
                if (noteList.size() > 1) {
                    noteListAdapter.notifyItemChanged(1);
                }
                Intent intent = new Intent(this, NoteEditActivity.class);
                intent.putExtra("noteDetail",emptyNote);
                startActivity(intent);
            } else {
                Toast.makeText(NoteListActivity.this, "app出错了，错误代码：404-1", Toast.LENGTH_LONG).show();
            }

        });
        footerText = findViewById(R.id.tv_footer_text);

        folderInfo = (FolderInfo) getIntent().getSerializableExtra("folderInfo");
        if (folderInfo == null) {
            return;
        }

        database = new Database(this);

        TextView title = findViewById(R.id.tv_note_list_title);
        title.setText(folderInfo.getName());

        RecyclerView recyclerView = findViewById(R.id.rv_note_list_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Database database = new Database(this);
        noteList = database.getNoteCards(folderInfo.getId());
        noteListAdapter = new NoteListAdapter(noteList, new NoteListAdapter.NoteListListener() {
            @Override
            public void onCountChanged(int count) {
                onNoteCountChanged(count);
            }

            @Override
            public void onItemClicked(NoteCard noteCard) {
                NoteDetail noteDetail = database.getNoteDetail(noteCard.getId());
                Intent intent = new Intent(NoteListActivity.this, NoteEditActivity.class);
                intent.putExtra("noteDetail", noteDetail);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(NoteCard noteCard) {
                NotePanelDialog dialog = new NotePanelDialog(noteCard, new NotePanelDialog.NotePaneDialogListener() {
                    @Override
                    public void onItemUpdated(NoteCard noteCard) {
                        if (database.updateNoteCard(noteCard.getId(), noteCard.getFolder().getId(), noteCard.getLocation())) {
                            if (!Objects.equals(noteCard.getFolder().getId(), folderInfo.getId())) {
                                for (int i = 0; i < noteList.size(); i++) {
                                    if (noteList.get(i).getId().equals(noteCard.getId())) {
                                        noteList.remove(i);

                                        noteListAdapter.notifyItemRemoved(i);
                                        if (i == 0) {
                                            noteListAdapter.notifyItemChanged(0);
                                        } else if (i == noteList.size()) {
                                            noteListAdapter.notifyItemChanged(i - 1);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
            }

            @Override
            public boolean onItemRemove(NoteCard noteCard) {
                return database.deleteNote(noteCard.getId());
            }
        });
        recyclerView.setAdapter(noteListAdapter);

        NoteListSwipe noteListSwipe = new NoteListSwipe(noteListAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(noteListSwipe);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        onNoteCountChanged(noteList.size());

        LocalBroadcastManager.getInstance(this).registerReceiver(noteUpdatedReceiver, new IntentFilter(getPackageName() + ".NOTE_UPDATED"));



        searchFilter = new SearchFilter();

        LinearLayout searchBox = findViewById(R.id.search_input);
        GridLayout searchFilterGird = findViewById(R.id.search_filter);
        EditText searchInput = searchBox.findViewById(R.id.et_search_box);

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
        searchInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchFilterGird.setVisibility(View.VISIBLE);
                }
            }
        });
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFilter.setKeyword(s.toString());
                updateNoteList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        CheckBox filterTitle = searchFilterGird.findViewById(R.id.cb_search_filter_title);
        CheckBox filterContent = searchFilterGird.findViewById(R.id.cb_search_filter_content);
        CheckBox filterLocation = searchFilterGird.findViewById(R.id.cb_search_filter_location);
        CheckBox filterDate = searchFilterGird.findViewById(R.id.cb_search_filter_date);
        CalendarView filterDateSelect = searchFilterGird.findViewById(R.id.cv_search_filter_time_select);
        searchFilterGird.setVisibility(View.GONE);
        filterDateSelect.setVisibility(View.GONE);
        filterTitle.setChecked(searchFilter.getTitle());
        filterContent.setChecked(searchFilter.getContent());
        filterLocation.setChecked(searchFilter.getLocation());
        filterTitle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            searchFilter.setTitle(isChecked);
            updateNoteList();
        });
        filterContent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            searchFilter.setContent(isChecked);
            updateNoteList();
        });
        filterLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            searchFilter.setLocation(isChecked);
            updateNoteList();
        });
        filterDate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                filterDateSelect.setVisibility(View.VISIBLE);

                long currentTimeMillis = System.currentTimeMillis();
                long startTimeMillis = currentTimeMillis - (currentTimeMillis % (24 * 60 * 60 * 1000));
                filterDateSelect.setDate(startTimeMillis);
                searchFilter.setDate(startTimeMillis);
                updateNoteList();
            } else {
                filterDateSelect.setVisibility(View.GONE);
                searchFilter.setDate(null);
                updateNoteList();
            }
        });
        filterDateSelect.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDate.set(Calendar.MILLISECOND, 0);
            searchFilter.setDate(selectedDate.getTimeInMillis());
            updateNoteList();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(noteUpdatedReceiver);
    }

    private void onNoteCountChanged(int count) {
        footerText.setText(getString(R.string.note_list_count, String.valueOf(count)));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateNoteList() {
        List<NoteCard> newNoteList = database.getNoteCards(folderInfo.getId(), searchFilter);
        noteList.clear();
        noteList.addAll(newNoteList);
        noteListAdapter.notifyDataSetChanged();
        onNoteCountChanged(noteList.size());
    }
}