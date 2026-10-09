package com.example.appmynotes.model;

public class Priority {
    private final long id;
    private final String description;
    private final String color;

    public Priority(long id, String description, String color) {
        this.id = id;
        this.description = description;
        this.color = color;
    }

    public long getId() { return id; }
    public String getDescription() { return description; }
    public String getColor() { return color; }

    @Override
    public String toString() { return description; }
}
