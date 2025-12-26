package com.example.myroom.domain.recommand.model;

import java.time.LocalDateTime;

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
@Table(name = "room_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;
    private String imageUrl;
    private LocalDateTime createdAt;

    @Builder
    public RoomImage(Long memberId, String imageUrl, LocalDateTime createdAt) {
        this.memberId = memberId;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
