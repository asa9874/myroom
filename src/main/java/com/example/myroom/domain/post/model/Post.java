package com.example.myroom.domain.post.model;

import java.time.LocalDateTime;

import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.model3D.model.Model3D;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model3d_id")
    private Model3D model3D;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private VisibilityScope visibilityScope = VisibilityScope.PUBLIC;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Post(Member member, Model3D model3D, String title, String content, Category category, VisibilityScope visibilityScope) {
        this.member = member;
        this.model3D = model3D;
        this.title = title;
        this.content = content;
        this.category = category;
        this.visibilityScope = visibilityScope != null ? visibilityScope : VisibilityScope.PUBLIC;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void update(String title, String content, Category category, VisibilityScope visibilityScope, Model3D model3D) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (category != null) {
            this.category = category;
        }
        if (visibilityScope != null) {
            this.visibilityScope = visibilityScope;
        }
        if (model3D != null) {
            this.model3D = model3D;
        }
    }
}