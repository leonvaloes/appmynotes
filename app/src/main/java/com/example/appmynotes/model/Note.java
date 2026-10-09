package com.example.appmynotes.model;

public class Note {
    private final long id;
    private final String title;
    private final String text;
    private final Priority priority;

    public Note(long id, String title, String text, Priority priority) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.priority = priority;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getText() { return text; }
    public Priority getPriority() { return priority; }
}
