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
@Table(name = "recommended_model")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendedModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private RecommendationSession recommendationSession;

    private Long modelId;
    private Float similarityScore;

    @Column(columnDefinition = "TEXT")
    private String recommendationReason;

    @Builder
    public RecommendedModel(RecommendationSession recommendationSession, Long modelId, Float similarityScore, String recommendationReason) {
        this.recommendationSession = recommendationSession;
        this.modelId = modelId;
        this.similarityScore = similarityScore;
        this.recommendationReason = recommendationReason;
    }

    public void update(Float similarityScore, String recommendationReason) {
        if (similarityScore != null) {
            this.similarityScore = similarityScore;
        }
        if (recommendationReason != null) {
            this.recommendationReason = recommendationReason;
        }
    }
}
