package com.skincare.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReviewAnalyticsDTO {
    // Tổng quan
    private long totalReviews;
    private double averageRating;
    private double responseRate;
    private double avgResponseTime;
    
    // Tỷ lệ tăng trưởng
    private double reviewsGrowth;
    private double ratingGrowth;
    private double responseRateGrowth;
    private double responseTimeGrowth;
    
    // Xu hướng và phân bố
    private List<RatingTrendPointDTO> ratingTrend;
    private Map<Integer, Long> ratingDistribution;
    
    // Hiệu suất theo chuyên viên và dịch vụ
    private List<SpecialistPerformanceDTO> specialistPerformance;
    private List<ServiceRatingDTO> serviceRatings;
    
    @Data
    public static class RatingTrendPointDTO {
        private String date;
        private Double averageRating;
        private Long totalReviews;
    }
    
    @Data
    public static class SpecialistPerformanceDTO {
        private Long specialistId;
        private String specialistName;
        private Double averageRating;
        private Long totalReviews;
        private Double responseRate;
        private Double avgResponseTime;
    }
    
    @Data
    public static class ServiceRatingDTO {
        private Long serviceId;
        private String serviceName;
        private Double averageRating;
        private Long totalReviews;
        private Map<Integer, Long> ratingDistribution;
    }
} 