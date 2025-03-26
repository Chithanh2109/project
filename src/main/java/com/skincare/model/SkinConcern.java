package com.skincare.model;

/**
 * Enum đại diện cho các vấn đề về da
 */
public enum SkinConcern {
    ACNE("Mụn"),
    AGING("Lão hóa"),
    DRYNESS("Khô da"),
    DULLNESS("Da xỉn màu"),
    DARK_SPOTS("Đốm đen"),
    WRINKLES("Nếp nhăn"),
    UNEVEN_TONE("Màu da không đều"),
    REDNESS("Da đỏ"),
    ENLARGED_PORES("Lỗ chân lông to"),
    HYDRATION("Thiếu nước");
    
    private final String displayName;
    
    SkinConcern(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 