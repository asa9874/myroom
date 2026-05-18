package com.example.myroom.fake.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fake_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FakeRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String roomName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String drawingImageUrl;

    @Column(nullable = false)
    private String xmlFileUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public FakeRoom(String roomName, String description, String drawingImageUrl, String xmlFileUrl) {
        this.roomName = roomName;
        this.description = description;
        this.drawingImageUrl = drawingImageUrl;
        this.xmlFileUrl = xmlFileUrl;
    }

    public void updateInfo(String roomName, String description) {
        if (roomName != null) {
            this.roomName = roomName;
        }
        if (description != null) {
            this.description = description;
        }
    }

    public void updateXmlFileUrl(String xmlFileUrl) {
        this.xmlFileUrl = xmlFileUrl;
    }

    public void updateDrawingImageUrl(String drawingImageUrl) {
        this.drawingImageUrl = drawingImageUrl;
    }
}
