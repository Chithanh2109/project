package com.skincare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "skin_concerns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkinConcern {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @ManyToMany(mappedBy = "skinConcerns")
    private Set<Service> services = new HashSet<>();
    
    @ManyToMany(mappedBy = "skinConcerns")
    private Set<CustomerProfile> customerProfiles = new HashSet<>();
    
    @ManyToMany(mappedBy = "recommendedFor")
    private Set<Product> recommendedProducts = new HashSet<>();
} 