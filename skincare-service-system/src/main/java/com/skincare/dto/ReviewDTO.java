package com.skincare.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private String customerName;
    private String specialistName;
    private String services;
    private Integer rating;
    private String comment;
    private String specialistResponse;
    private boolean flagged;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
} 