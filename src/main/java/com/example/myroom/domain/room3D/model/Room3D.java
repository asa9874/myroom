package com.example.myroom.domain.room3D.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.myroom.domain.member.model.Member;

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
@Table(name = "room3d")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room3D {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 150)
    private String roomName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String drawingImageUrl;

    private String drawingXmlUrl;

    private Boolean success;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Room3D(
            Member member,
            String roomName,
            String description,
            String drawingImageUrl,
            String drawingXmlUrl,
            Boolean success) {
        this.member = member;
        this.roomName = roomName;
        this.description = description;
        this.drawingImageUrl = drawingImageUrl;
        this.drawingXmlUrl = drawingXmlUrl;
        this.success = success;
    }

    public void updateInfo(String roomName, String description) {
        if (roomName != null) {
            this.roomName = roomName;
        }
        if (description != null) {
            this.description = description;
        }
    }

    public void updateXmlFileUrl(String drawingXmlUrl) {
        this.drawingXmlUrl = drawingXmlUrl;
    }

    public void updateAiResult(Boolean success, String drawingXmlUrl) {
        this.success = success;
        this.drawingXmlUrl = drawingXmlUrl;
    }
}
