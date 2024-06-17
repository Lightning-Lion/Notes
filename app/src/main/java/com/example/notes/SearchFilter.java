package com.example.notes;

import java.util.Date;

public class SearchFilter {
    private String keyword;
    private boolean title;
    private boolean content;
    private boolean location;
    private Long date;
    public SearchFilter() {
        keyword = "";
        title = false;
        content = true;
        location = false;
        date = null;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean getTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public boolean getContent() {
        return content;
    }

    public void setContent(boolean content) {
        this.content = content;
    }

    public boolean getLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
