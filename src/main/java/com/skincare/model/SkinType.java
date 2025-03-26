package com.skincare.model;

/**
 * Enum đại diện cho các loại da
 */
public enum SkinType {
    DRY("Da khô"),
    OILY("Da dầu"),
    COMBINATION("Da hỗn hợp"),
    NORMAL("Da thường"),
    SENSITIVE("Da nhạy cảm");

    private final String displayName;

    SkinType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 