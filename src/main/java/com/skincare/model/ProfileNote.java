package com.skincare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileNote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_profile_id")
    private CustomerProfile customerProfile;
    
    @Column(nullable = false, length = 1000)
    private String note;
    
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    private LocalDateTime createdAt = LocalDateTime.now();
} 