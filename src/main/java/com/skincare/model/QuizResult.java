package com.skincare.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    @ElementCollection
    @CollectionTable(
        name = "result_skin_concern",
        joinColumns = @JoinColumn(name = "result_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "skin_concern")
    private Set<SkinConcern> identifiedConcerns = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "result_recommended_service",
        joinColumns = @JoinColumn(name = "result_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> recommendedServices = new HashSet<>();
} 