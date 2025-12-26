package com.example.myroom.domain.model3D.dto.request;

public record Model3DCreateRequestDto(
        String link,
        Boolean isShared,
        String description
) {
    
}
