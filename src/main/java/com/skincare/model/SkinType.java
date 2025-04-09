package com.skincare.model;

public enum SkinType {
    NORMAL("Da thường"),           // Normal skin
    DRY("Da khô"),                // Dry skin
    OILY("Da dầu"),               // Oily skin
    COMBINATION("Da hỗn hợp"),     // Combination skin
    SENSITIVE("Da nhạy cảm");      // Sensitive skin

    private final String description;

    SkinType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 