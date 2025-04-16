package com.skincare.model;

public enum SkinConcern {
    ACNE("Mụn"),
    AGING("Lão hóa"),
    DARK_SPOTS("Đốm nâu"),
    DRYNESS("Khô da"),
    HYPERPIGMENTATION("Tăng sắc tố"),
    LARGE_PORES("Lỗ chân lông to"),
    REDNESS("Đỏ da"),
    SENSITIVITY("Nhạy cảm"),
    WRINKLES("Nếp nhăn");

    private final String description;

    SkinConcern(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 