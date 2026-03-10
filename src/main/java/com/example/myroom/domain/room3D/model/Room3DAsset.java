package com.example.myroom.domain.room3D.model;

import jakarta.persistence.Column;
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
public class Room3DAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "model_url", nullable = false)
    private String modelUrl;

    @Builder
    public Room3DAsset(String name, String description, String thumbnailUrl, String modelUrl) {
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.modelUrl = modelUrl;
    }
}
