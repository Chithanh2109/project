package com.skincare.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_services")
@Data
@NoArgsConstructor
public class AppointmentService {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;
    
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(length = 1000)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceStatus status = ServiceStatus.PENDING;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @ManyToOne
    @JoinColumn(name = "performed_by_id")
    private User performedBy;
    
    public enum ServiceStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
} 