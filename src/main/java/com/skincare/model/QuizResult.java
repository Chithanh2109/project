package com.skincare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "quiz_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    @Column(length = 1000)
    private String resultSummary;
    
    private LocalDateTime completedAt = LocalDateTime.now();
    
    @ManyToMany
    @JoinTable(
        name = "result_skin_concern",
        joinColumns = @JoinColumn(name = "result_id"),
        inverseJoinColumns = @JoinColumn(name = "skin_concern_id")
    )
    private Set<SkinConcern> identifiedConcerns = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "result_recommended_service",
        joinColumns = @JoinColumn(name = "result_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> recommendedServices = new HashSet<>();
} 