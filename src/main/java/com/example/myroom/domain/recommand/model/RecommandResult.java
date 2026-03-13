package com.example.myroom.domain.recommand.model;

import java.util.ArrayList;
import java.util.List;

import com.example.myroom.domain.model3D.model.Model3D;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommand_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommandResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false)
    private RecommandRecommendation recommendation;

    private Integer rankIndex;
    private Double score;

        @ManyToMany
        @JoinTable(
            name = "recommand_result_model3d",
            joinColumns = @JoinColumn(name = "recommand_result_id"),
            inverseJoinColumns = @JoinColumn(name = "model3d_id")
        )
        private List<Model3D> model3Ds = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Builder
    public RecommandResult(
            Integer rankIndex,
            Double score,
            String metadataJson) {
        this.rankIndex = rankIndex;
        this.score = score;
        this.metadataJson = metadataJson;
    }

    void assignRecommendation(RecommandRecommendation recommendation) {
        this.recommendation = recommendation;
    }

    public void addModel3D(Model3D model3D) {
        if (model3D != null) {
            this.model3Ds.add(model3D);
        }
    }
}
