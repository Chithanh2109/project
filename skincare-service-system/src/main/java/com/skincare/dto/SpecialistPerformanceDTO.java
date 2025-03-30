package com.skincare.dto;

import lombok.Data;

@Data
public class SpecialistPerformanceDTO {
    private String specialistName;
    private double averageRating;
    private long totalReviews;
    private double responseRate;
    private double avgResponseTime;
} 