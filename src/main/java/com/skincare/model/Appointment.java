package com.skincare.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime appointmentDate;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;
    
    @Column(length = 1000)
    private String note;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    
    @ManyToOne
    @JoinColumn(name = "therapist_id")
    private User therapist;
    
    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    private User assignedBy;
    
    private LocalDateTime checkinTime;
    
    private LocalDateTime checkoutTime;
    
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AppointmentService> services = new HashSet<>();
    
    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Feedback feedback;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(length = 50)
    private String bookingCode;
    
    private String createdBy;
    
    private String updatedBy;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum AppointmentStatus {
        PENDING,
        CONFIRMED,
        CHECKED_IN,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
} 