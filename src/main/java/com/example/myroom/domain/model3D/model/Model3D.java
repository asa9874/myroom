package com.example.myroom.domain.model3D.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Model3D {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private String link;
    private Long creatorId;
    private Boolean isShared = false;
    private String description;
    private String thumbnailUrl;

    @Builder
    public Model3D(String name, LocalDateTime createdAt, String link, Long creatorId, Boolean isShared, String description, String thumbnailUrl) {
        this.name = name;
        this.createdAt = createdAt;
        this.link = link;
        this.creatorId = creatorId;
        this.isShared = isShared != null ? isShared : false;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void update(String name, Boolean isShared, String description) {
        if (name != null) {
            this.name = name;
        }
        if (isShared != null) {
            this.isShared = isShared;
        }
        if (description != null) {
            this.description = description;
        }
    }

    public void adminUpdate(String name, String link, Long creatorId, Boolean isShared, String description) {
        if (name != null) {
            this.name = name;
        }
        if (link != null) {
            this.link = link;
        }
        if (creatorId != null) {
            this.creatorId = creatorId;
        }
        if (isShared != null) {
            this.isShared = isShared;
        }
        if (description != null) {
            this.description = description;
        }
    }
}
