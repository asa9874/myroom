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
    private LocalDateTime createdAt;
    private String link;
    private Long creatorId;
    private Boolean isShared = false;
    private String description;

    @Builder
    public Model3D(LocalDateTime createdAt, String link, Long creatorId, Boolean isShared, String description) {
        this.createdAt = createdAt;
        this.link = link;
        this.creatorId = creatorId;
        this.isShared = isShared != null ? isShared : false;
        this.description = description;
    }

    public void update(String link, Boolean isShared, String description) {
        if (link != null) {
            this.link = link;
        }
        if (isShared != null) {
            this.isShared = isShared;
        }
        if (description != null) {
            this.description = description;
        }
    }

    public void adminUpdate(String link, Long creatorId, Boolean isShared, String description) {
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
