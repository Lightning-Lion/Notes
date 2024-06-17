package com.example.notes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.Editable;
import android.util.Log;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {
    Database(Context context) {
        super(context, "default", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void init() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS Notes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER," +
                "folder_id INTEGER DEFAULT 1," +
                "title TEXT DEFAULT ''," +
                "summary TEXT DEFAULT ''," +
                "content TEXT DEFAULT ''," +
                "time LONG DEFAULT 0," +
                "location TEXT DEFAULT '')");
        db.execSQL("CREATE TABLE IF NOT EXISTS Folders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER," +
                "name TEXT NOT NULL DEFAULT '')");
        db.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL)");
    }

    public NoteCard getNoteCard(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Notes.folder_id, Folders.name AS folder_name, Notes.title, Notes.summary, Notes.time, Notes.location FROM Notes Join Folders ON Folders.id = Notes.folder_id WHERE Notes.id = ? AND Notes.user_id = ?", new String[]{id, GlobalVariables.userId});

        if (cursor != null && cursor.moveToFirst()) {

            @SuppressLint("Range") NoteCard noteCard = new NoteCard(
                    id,
                    new FolderInfo(cursor.getString(cursor.getColumnIndex("folder_id")), cursor.getString(cursor.getColumnIndex("folder_name"))),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("summary")),
                    cursor.getLong(cursor.getColumnIndex("time")),
                    cursor.getString(cursor.getColumnIndex("location"))
            );
            cursor.close();

            return noteCard;
        }

        throw new SQLiteException();
    }

    public NoteDetail getNoteDetail(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT folder_id, title, content FROM Notes WHERE id = ? AND user_id = ?", new String[]{id, GlobalVariables.userId});

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") NoteDetail noteDetail = new NoteDetail(
                    id,
                    cursor.getString(cursor.getColumnIndex("folder_id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    null,
                    cursor.getString(cursor.getColumnIndex("content"))
            );
            cursor.close();

            return noteDetail;
        }

        throw new SQLiteException();
    }

    public ArrayList<NoteCard> getNoteCards(String folderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Notes.id, Folders.name as folder_name, Notes.title, Notes.summary, Notes.time, Notes.location FROM Notes Join Folders ON Folders.id = Notes.folder_id WHERE Notes.folder_id = ? AND Notes.user_id = ? ORDER BY time DESC", new String[]{folderId, GlobalVariables.userId});

        ArrayList<NoteCard> noteList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") NoteCard noteCard = new NoteCard(
                        cursor.getString(cursor.getColumnIndex("id")),
                        new FolderInfo(folderId, cursor.getString(cursor.getColumnIndex("folder_name"))),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("summary")),
                        cursor.getLong(cursor.getColumnIndex("time")),
                        cursor.getString(cursor.getColumnIndex("location"))
                );
                noteList.add(noteCard);
            }
            cursor.close();
        }
        return noteList;
    }

    public ArrayList<NoteCard> getNoteCards(String folderId, SearchFilter searchFilter) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> variablesList = new ArrayList<>();
        variablesList.add(folderId);
        variablesList.add(GlobalVariables.userId);

        String add = "";
        if (!searchFilter.getKeyword().isEmpty()) {
            if (searchFilter.getTitle()) {
                add += " AND Notes.title LIKE ?";
                variablesList.add("%" + searchFilter.getKeyword() + "%");
            }
            if (searchFilter.getContent()) {
                add += " AND Notes.summary LIKE ?";
                variablesList.add("%" + searchFilter.getKeyword() + "%");
            }
            if (searchFilter.getLocation()) {
                add += " AND Notes.location LIKE ?";
                variablesList.add("%" + searchFilter.getKeyword() + "%");
            }
        }
        if (searchFilter.getDate() != null) {
            add += " AND Notes.time >= ? AND Notes.time <= ?";
            variablesList.add(searchFilter.getDate().toString());
            variablesList.add(String.valueOf(searchFilter.getDate() + 1000 * 60 * 60 * 24));
            Log.d("da", searchFilter.getDate().toString());
        }

        Cursor cursor = db.rawQuery("SELECT Notes.id, Folders.name as folder_name, Notes.title, Notes.summary, Notes.time, Notes.location FROM Notes Join Folders ON Folders.id = Notes.folder_id WHERE Notes.folder_id = ? AND Notes.user_id = ?" + add + " ORDER BY time DESC", variablesList.toArray(new String[0]));

        ArrayList<NoteCard> noteList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") NoteCard noteCard = new NoteCard(
                        cursor.getString(cursor.getColumnIndex("id")),
                        new FolderInfo(folderId, cursor.getString(cursor.getColumnIndex("folder_name"))),
                        cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("summary")),
                        cursor.getLong(cursor.getColumnIndex("time")),
                        cursor.getString(cursor.getColumnIndex("location"))
                );
                noteList.add(noteCard);
            }
            cursor.close();
        }
        return noteList;
    }

    public boolean deleteNote(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("DELETE FROM Notes WHERE id = ? AND user_id = ?");
        stmt.bindString(1, id);
        stmt.bindString(2, GlobalVariables.userId);

        int affectedRows = stmt.executeUpdateDelete();
        return affectedRows > 0;
    }

    public boolean updateNoteCard(String id, String folderId, String location) {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("UPDATE Notes SET folder_id = ?, location = ? WHERE id = ? AND user_id = ?");
        stmt.bindString(1, folderId);
        stmt.bindString(2, location);
        stmt.bindString(3, id);
        stmt.bindString(4, GlobalVariables.userId);

        int affectedRows = stmt.executeUpdateDelete();
        return affectedRows != 0;
    }

    public boolean updateNoteDetail(String id, String title, String summary, String content) {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("UPDATE Notes SET title = ?, summary = ?, content = ?, time = ? WHERE id = ? AND user_id = ?");
        stmt.bindString(1, title);
        stmt.bindString(2, summary);
        stmt.bindString(3, content);
        stmt.bindLong(4, System.currentTimeMillis());
        stmt.bindString(5, id);
        stmt.bindString(6, GlobalVariables.userId);

        int affectedRows = stmt.executeUpdateDelete();
        return affectedRows != 0;
    }

    public String insertNote(String folderId, String title, String summary, String content) {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("INSERT INTO Notes (user_id, folder_id, title, summary, content, time) VALUES (?, ?, ?, ?, ?, ?)");
        stmt.bindString(1, GlobalVariables.userId);
        stmt.bindString(2, folderId);
        stmt.bindString(3, title);
        stmt.bindString(4, summary);
        stmt.bindString(5, content);
        stmt.bindLong(6, System.currentTimeMillis());

        return String.valueOf(stmt.executeInsert());
    }

    public boolean updateFolder(String id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("UPDATE Folders SET name = ? WHERE id = ? AND user_id = ?");
        stmt.bindString(1, name);
        stmt.bindString(2, id);
        stmt.bindString(3, GlobalVariables.userId);

        int affectedRows = stmt.executeUpdateDelete();
        return affectedRows != 0;
    }

    public ArrayList<FolderInfo> getFolderList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d("tmp", GlobalVariables.userId);
        Cursor cursor = db.rawQuery("SELECT id, name FROM Folders WHERE user_id = ?", new String[]{GlobalVariables.userId});

        ArrayList<FolderInfo> folderList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") FolderInfo folderInfo = new FolderInfo(
                        cursor.getString(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name"))
                );
                folderList.add(folderInfo);
            }
            cursor.close();
        }
        return folderList;
    }

    public String insertFolder(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("INSERT INTO Folders (user_id, name) VALUES (?, ?)");
        stmt.bindString(1, GlobalVariables.userId);
        stmt.bindString(2, name);

        return String.valueOf(stmt.executeInsert());
    }

    public boolean deleteFolder(String folderId) {
        SQLiteDatabase db = this.getWritableDatabase();

        SQLiteStatement stmt = db.compileStatement("DELETE FROM Folders WHERE id = ? AND user_id = ?");
        stmt.bindString(1, folderId);
        stmt.bindString(2, GlobalVariables.userId);

        int affectedRows = stmt.executeUpdateDelete();
        return affectedRows > 0;
    }

    public boolean loginOrRegister(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, password FROM Users WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
            @SuppressLint("Range") String hashedPassword = cursor.getString(cursor.getColumnIndex("password"));
            cursor.close();

            if (Utils.sha256(password).equals(hashedPassword)) {
                GlobalVariables.userId = id;
                return true;
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }

            SQLiteStatement stmt = db.compileStatement("INSERT INTO Users (username, password) VALUES (?, ?)");
            stmt.bindString(1, username);
            stmt.bindString(2, Utils.sha256(password));

            if (stmt.executeInsert() != -1) {
                Cursor cursor2 = db.rawQuery("SELECT id FROM Users WHERE username = ?", new String[]{username});
                if (cursor2 != null && cursor2.moveToFirst()) {
                    @SuppressLint("Range") String id = cursor2.getString(cursor2.getColumnIndex("id"));
                    cursor2.close();

                    SQLiteStatement stmt2 = db.compileStatement("INSERT INTO Folders (user_id, name) VALUES (?, '默认分类')");
                    stmt2.bindString(1, id);

                    if (stmt2.executeInsert() != -1) {
                        GlobalVariables.userId = id;
                        return true;
                    }
                }
                if (cursor2 != null) {
                    cursor2.close();
                }
            }
        }
        return false;
    }
}
