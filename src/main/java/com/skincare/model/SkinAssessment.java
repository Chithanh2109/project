package com.skincare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skin_assessments")
public class SkinAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "assessment_date", nullable = false)
    private LocalDateTime assessmentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_type", nullable = false)
    private SkinType skinType;

    @ElementCollection
    @CollectionTable(name = "skin_concerns", joinColumns = @JoinColumn(name = "assessment_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "concern")
    private Set<SkinConcern> skinConcerns = new HashSet<>();

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "recommendations", length = 1000)
    private String recommendations;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
        name = "assessment_services",
        joinColumns = @JoinColumn(name = "assessment_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> recommendedServices = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public SkinType getSkinType() {
        return skinType;
    }

    public void setSkinType(SkinType skinType) {
        this.skinType = skinType;
    }

    public Set<SkinConcern> getSkinConcerns() {
        return skinConcerns;
    }

    public void setSkinConcerns(Set<SkinConcern> skinConcerns) {
        this.skinConcerns = skinConcerns;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Service> getRecommendedServices() {
        return recommendedServices;
    }

    public void setRecommendedServices(Set<Service> recommendedServices) {
        this.recommendedServices = recommendedServices;
    }
} 