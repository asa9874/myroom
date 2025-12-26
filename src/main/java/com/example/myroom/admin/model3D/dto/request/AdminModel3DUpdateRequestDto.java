package com.example.myroom.admin.model3D.dto.request;

public record AdminModel3DUpdateRequestDto(
        String link,
        Long creatorId,
        Boolean isShared,
        String description
) {
    
}
