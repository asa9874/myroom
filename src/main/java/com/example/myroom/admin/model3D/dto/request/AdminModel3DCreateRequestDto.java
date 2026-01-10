package com.example.myroom.admin.model3D.dto.request;

public record AdminModel3DCreateRequestDto(
        String name,
        String link,
        Long creatorId,
        Boolean isShared,
        String description
) {
    
}
