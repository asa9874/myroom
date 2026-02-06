package com.example.myroom.domain.post.model;

public enum VisibilityScope {
    PUBLIC("공개"),
    PRIVATE("비공개");

    private final String description;

    VisibilityScope(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}