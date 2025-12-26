package com.example.myroom.domain.model3D.dto.response;

import java.time.LocalDateTime;

import com.example.myroom.domain.model3D.model.Model3D;

public record Model3DResponseDto(
        Long id,
        LocalDateTime createdAt,
        String link,
        Long creatorId,
        Boolean isShared,
        String description
) {
    public static Model3DResponseDto from(Model3D model3D) {
        return new Model3DResponseDto(
                model3D.getId(),
                model3D.getCreatedAt(),
                model3D.getLink(),
                model3D.getCreatorId(),
                model3D.getIsShared(),
                model3D.getDescription()
        );
    }
    
}
