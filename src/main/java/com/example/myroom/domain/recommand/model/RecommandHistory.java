package com.example.myroom.domain.recommand.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.myroom.domain.member.model.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommand_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommandHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 30)
    private String status;

    @OneToOne(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RecommandRoomAnalysis roomAnalysis;

    @OneToOne(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RecommandRecommendation recommendation;

    private Long responseTimestamp;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public RecommandHistory(
            Member member,
            String status,
            Long responseTimestamp) {
        this.member = member;
        this.status = status;
        this.responseTimestamp = responseTimestamp;
    }

    public void assignRoomAnalysis(RecommandRoomAnalysis roomAnalysis) {
        this.roomAnalysis = roomAnalysis;
        if (roomAnalysis != null) {
            roomAnalysis.assignHistory(this);
        }
    }

    public void assignRecommendation(RecommandRecommendation recommendation) {
        this.recommendation = recommendation;
        if (recommendation != null) {
            recommendation.assignHistory(this);
        }
    }
}
