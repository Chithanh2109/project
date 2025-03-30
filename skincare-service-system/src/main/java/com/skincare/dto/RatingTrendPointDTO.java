package com.skincare.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RatingTrendPointDTO {
    private LocalDate date;
    private double averageRating;
    private long totalReviews;
} 