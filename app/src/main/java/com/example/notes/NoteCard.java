package com.example.notes;

public class NoteCard {
    private String id;
    private FolderInfo folder;
    private String title;
    private String summary;
    private Long time;
    private String location;

    public NoteCard(String id, FolderInfo folder, String title, String summary, Long time, String location) {
        this.id = id;
        this.folder = folder;
        this.title = title;
        this.summary = summary;
        this.time = time;
        this.location = location;
    }

    public String getId() {
        return id;
    }
    public FolderInfo getFolder() {
        return folder;
    }
    public String getTitle() {
        return title;
    }
    public String getSummary() {
        return summary;
    }
    public Long getTime() {
        return time;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }


}
