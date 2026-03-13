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
@Table(name = "recommand_recommendation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommandRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false, unique = true)
    private RecommandHistory history;

    @Column(length = 50)
    private String targetCategory;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(columnDefinition = "TEXT")
    private String searchQuery;

    private Integer resultCount;

    @OrderBy("rankIndex ASC")
    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecommandResult> results = new ArrayList<>();

    @Builder
    public RecommandRecommendation(String targetCategory, String reasoning, String searchQuery, Integer resultCount) {
        this.targetCategory = targetCategory;
        this.reasoning = reasoning;
        this.searchQuery = searchQuery;
        this.resultCount = resultCount;
    }

    void assignHistory(RecommandHistory history) {
        this.history = history;
    }

    public void addResult(RecommandResult result) {
        result.assignRecommendation(this);
        this.results.add(result);
    }
}
