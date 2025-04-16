package com.skincare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.skincare.model.SkinConcern;

@Repository
public interface SkinConcernRepository {
    /**
     * Find all skin concerns
     * @return List of all skin concerns
     */
    List<SkinConcern> findAll();
    
    /**
     * Find a skin concern by its ID (ordinal value)
     * @param id The ID of the skin concern
     * @return Optional containing the skin concern if found
     */
    Optional<SkinConcern> findById(Long id);
    
    /**
     * Find a skin concern by its name
     * @param name The name of the skin concern
     * @return The skin concern, if found
     */
    SkinConcern findByName(String name);
    
    /**
     * Find a skin concern by its description
     * @param description The description of the skin concern
     * @return The skin concern, if found
     */
    SkinConcern findByDescription(String description);
} 