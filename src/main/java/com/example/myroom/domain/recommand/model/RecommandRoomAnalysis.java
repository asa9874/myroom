package com.example.myroom.domain.recommand.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommand_room_analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommandRoomAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false, unique = true)
    private RecommandHistory history;

    @Column(length = 50)
    private String style;

    @Column(length = 50)
    private String color;

    @Column(length = 50)
    private String material;

    private Integer detectedCount;

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "roomAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecommandDetectedFurniture> detectedFurnitures = new ArrayList<>();

    @OrderBy("id ASC")
    @OneToMany(mappedBy = "roomAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecommandDetectedItem> detailedDetections = new ArrayList<>();

    @Builder
    public RecommandRoomAnalysis(String style, String color, String material, Integer detectedCount) {
        this.style = style;
        this.color = color;
        this.material = material;
        this.detectedCount = detectedCount;
    }

    void assignHistory(RecommandHistory history) {
        this.history = history;
    }

    public void addDetectedFurniture(String furnitureName) {
        RecommandDetectedFurniture furniture = RecommandDetectedFurniture.builder()
                .furnitureName(furnitureName)
                .build();
        furniture.assignRoomAnalysis(this);
        this.detectedFurnitures.add(furniture);
    }

    public void addDetailedDetection(RecommandDetectedItem detectedItem) {
        detectedItem.assignRoomAnalysis(this);
        this.detailedDetections.add(detectedItem);
    }
}
