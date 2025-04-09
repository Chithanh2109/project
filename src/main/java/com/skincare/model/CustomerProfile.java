package com.skincare.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @OneToMany(mappedBy = "customerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProfileNote> notes = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "skin_type_id")
    private SkinType skinType;
    
    @OneToMany(mappedBy = "customerProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkinAssessment> assessments = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "customer_skin_concerns",
        joinColumns = @JoinColumn(name = "customer_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "skin_concern_id")
    )
    private List<SkinConcern> skinConcerns = new ArrayList<>();
}
