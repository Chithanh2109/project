package com.skincare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TherapistStat {
    private Long id;
    private String fullName;
    private int appointmentCount;
    private double averageRating;
    private int completedServices;
} 