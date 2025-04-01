package com.skincare.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String phone;
    
    private String address;
    
    private String notes;
    
    private String avatarUrl;
    
    @Column(nullable = false)
    private String role;
    
    @Column(nullable = false)
    private boolean active = true;
    
    // Thống kê
    @Column(name = "total_appointments")
    private int totalAppointments = 0;
    
    @Column(name = "average_rating")
    private double averageRating = 0.0;
    
    @Column(name = "total_spent")
    private double totalSpent = 0.0;
    
    @Column(name = "last_visit")
    private LocalDateTime lastVisit;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 