package com.example.myroom.domain.recommand.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "recommand_detected_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommandDetectedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_analysis_id", nullable = false)
    private RecommandRoomAnalysis roomAnalysis;

    @Column(length = 100)
    private String name;

    private Double confidence;

    private Double bboxX1;
    private Double bboxY1;
    private Double bboxX2;
    private Double bboxY2;

    @Builder
    public RecommandDetectedItem(
            String name,
            Double confidence,
            Double bboxX1,
            Double bboxY1,
            Double bboxX2,
            Double bboxY2) {
        this.name = name;
        this.confidence = confidence;
        this.bboxX1 = bboxX1;
        this.bboxY1 = bboxY1;
        this.bboxX2 = bboxX2;
        this.bboxY2 = bboxY2;
    }

    void assignRoomAnalysis(RecommandRoomAnalysis roomAnalysis) {
        this.roomAnalysis = roomAnalysis;
    }
}
