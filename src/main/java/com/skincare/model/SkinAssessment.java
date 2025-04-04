package com.skincenter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skin_assessments")
public class SkinAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(nullable = false)
    private LocalDateTime assessmentDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column
    private SkinType skinType;

    @ElementCollection
    @CollectionTable(name = "skin_concerns", joinColumns = @JoinColumn(name = "assessment_id"))
    @Enumerated(EnumType.STRING)
    private List<SkinConcern> skinConcerns = new ArrayList<>();

    @Column
    private Boolean hasSensitiveSkin;

    @Column
    private String allergies;

    @Column(length = 1000)
    private String additionalNotes;

    @ManyToMany
    @JoinTable(
        name = "recommended_services",
        joinColumns = @JoinColumn(name = "assessment_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<SkinCareService> recommendedServices = new ArrayList<>();
} 