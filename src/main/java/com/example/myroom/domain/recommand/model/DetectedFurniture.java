package com.example.myroom.domain.recommand.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detected_furniture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetectedFurniture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_image_id", nullable = false)
    private RoomImage roomImage;

    private String category;
    private Float confidence;

    @Column(columnDefinition = "TEXT")
    private String positionMetadata;

    @Builder
    public DetectedFurniture(RoomImage roomImage, String category, Float confidence, String positionMetadata) {
        this.roomImage = roomImage;
        this.category = category;
        this.confidence = confidence;
        this.positionMetadata = positionMetadata;
    }
}
