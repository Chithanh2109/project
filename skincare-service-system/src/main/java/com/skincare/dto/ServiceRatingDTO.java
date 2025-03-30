package com.skincare.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ServiceRatingDTO {
    private String serviceName;
    private double averageRating;
    private long totalReviews;
    private Map<Integer, Long> ratingDistribution;
} 