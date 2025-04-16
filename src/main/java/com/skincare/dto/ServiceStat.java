package com.skincare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStat {
    private Long id;
    private String name;
    private int bookingCount;
    private BigDecimal revenue;
} 