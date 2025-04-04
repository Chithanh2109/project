package com.skincenter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skin_care_services")
public class SkinCareService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private Integer durationMinutes;

    @Column
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column
    private ServiceCategory category;
    
    @Column
    private Boolean isActive = true;
} 