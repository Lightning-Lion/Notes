package com.example.notes;

import java.io.Serializable;

public class NoteDetail implements Serializable {
    private String id;
    private String folderId;
    private String title;
    private String summary;
    private String content;

    public NoteDetail(String id, String folderId, String title, String summary, String content) {
        this.id = id;
        this.folderId = folderId;
        this.title = title;
        this.summary = summary;
        this.content = content;
    }

    public String getId() {
        return id;
    }
    public String getFolderId() {
        return folderId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSummary() {
        return summary;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setSummary(String summary) {
        this.summary = summary;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
