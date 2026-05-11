package com.example.myroom.domain.bookmark.model;

import java.time.LocalDateTime;

import com.example.myroom.domain.member.model.Member;
import com.example.myroom.domain.model3D.model.Model3D;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "model3d_bookmark",
    uniqueConstraints = @UniqueConstraint(columnNames = {"model3d_id", "member_id"})
)
public class Model3DBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model3d_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Model3D model3D;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private LocalDateTime createdAt;

    @Builder
    public Model3DBookmark(Model3D model3D, Member member) {
        this.model3D = model3D;
        this.member = member;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
