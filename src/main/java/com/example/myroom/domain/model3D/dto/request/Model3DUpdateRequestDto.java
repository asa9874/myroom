package com.example.myroom.domain.model3D.dto.request;

public record Model3DUpdateRequestDto(
        String link,
        Boolean isShared,
        String description
) {
    
}
