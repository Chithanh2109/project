package com.skincare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalAppointments;
    private long totalCustomers;
    private long totalTherapists;
    private long completedAppointments;
    private long cancelledAppointments;
    private double completionRate;
    private BigDecimal revenue;
    private double growthRate;
} 