package com.example.myroom.domain.model3D.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * FurnitureCategory enum을 DB에 소문자 문자열로 저장하기 위한 Converter
 */
@Converter(autoApply = true)
public class FurnitureCategoryConverter implements AttributeConverter<FurnitureCategory, String> {

    @Override
    public String convertToDatabaseColumn(FurnitureCategory category) {
        if (category == null) {
            return null;
        }
        return category.getDbValue();
    }

    @Override
    public FurnitureCategory convertToEntityAttribute(String dbValue) {
        if (dbValue == null || dbValue.trim().isEmpty()) {
            return null;
        }
        return FurnitureCategory.fromString(dbValue);
    }
}
