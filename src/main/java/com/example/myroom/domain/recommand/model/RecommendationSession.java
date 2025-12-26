package com.example.myroom.domain.recommand.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommendation_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "room_image_id", nullable = false, unique = true)
    private RoomImage roomImage;

    @Column(columnDefinition = "TEXT")
    private String styleCaption;

    @Column(columnDefinition = "TEXT")
    private String aiDesignerAdvice;

    private LocalDateTime recommendedAt;

    @Builder
    public RecommendationSession(RoomImage roomImage, String styleCaption, String aiDesignerAdvice, LocalDateTime recommendedAt) {
        this.roomImage = roomImage;
        this.styleCaption = styleCaption;
        this.aiDesignerAdvice = aiDesignerAdvice;
        this.recommendedAt = recommendedAt != null ? recommendedAt : LocalDateTime.now();
    }

    public void update(String styleCaption, String aiDesignerAdvice) {
        if (styleCaption != null) {
            this.styleCaption = styleCaption;
        }
        if (aiDesignerAdvice != null) {
            this.aiDesignerAdvice = aiDesignerAdvice;
        }
    }
}
