package com.example.myroom.domain.model3D.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 가구 카테고리 enum
 */
public enum FurnitureCategory {
    SHELF("shelf", "선반"),
    SOFA("sofa", "소파"),
    STORAGE("storage", "수납장"),
    CHAIR("chair", "의자"),
    LIGHTING("lighting", "조명"),
    DESK("desk", "책상"),
    BED("bed", "침대"),
    TABLE("table", "테이블"),
    OTHERS("others", "others");

    private final String dbValue;  // DB에 저장될 소문자 값
    private final String koreanName;

    FurnitureCategory(String dbValue, String koreanName) {
        this.dbValue = dbValue;
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getDbValue() {
        return dbValue;
    }

    public String getKoreanName() {
        return koreanName;
    }

    @JsonCreator
    public static FurnitureCategory fromString(String value) {
        if (value == null) {
            return null;
        }
        
        // DB 값(소문자 영문)으로 매칭
        for (FurnitureCategory category : FurnitureCategory.values()) {
            if (category.dbValue.equalsIgnoreCase(value)) {
                return category;
            }
        }
        
        // 한글 이름으로 매칭
        for (FurnitureCategory category : FurnitureCategory.values()) {
            if (category.koreanName.equalsIgnoreCase(value)) {
                return category;
            }
        }
        
        // enum 이름으로 매칭 (대소문자 무시)
        for (FurnitureCategory category : FurnitureCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        
        // 매칭 실패시 others로 처리
        return OTHERS;
    }

    @Override
    public String toString() {
        return dbValue;
    }
}
