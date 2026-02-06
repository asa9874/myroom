package com.example.myroom.domain.post.model;

public enum Category {
    FURNITURE("가구"),
    INTERIOR("인테리어"), 
    QUESTION("질문"),
    REVIEW("후기"),
    ETC("기타");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}