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
@Table(name = "recommand_detected_furniture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommandDetectedFurniture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_analysis_id", nullable = false)
    private RecommandRoomAnalysis roomAnalysis;

    @Column(nullable = false, length = 100)
    private String furnitureName;

    @Builder
    public RecommandDetectedFurniture(String furnitureName) {
        this.furnitureName = furnitureName;
    }

    void assignRoomAnalysis(RecommandRoomAnalysis roomAnalysis) {
        this.roomAnalysis = roomAnalysis;
    }
}
