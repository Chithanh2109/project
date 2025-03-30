package com.skincare.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer_profiles")
@Data
@NoArgsConstructor
public class CustomerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(length = 500)
    private String skinType;
    
    @Column(length = 1000)
    private String allergies;
    
    @Column(length = 1000)
    private String medicalHistory;
    
    @Column(length = 1000)
    private String skinCareRoutine;
    
    @Column(length = 1000)
    private String notes;
    
    @ManyToMany
    @JoinTable(
        name = "customer_skin_concerns",
        joinColumns = @JoinColumn(name = "customer_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "skin_concern_id")
    )
    private Set<SkinConcern> skinConcerns = new HashSet<>();
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 