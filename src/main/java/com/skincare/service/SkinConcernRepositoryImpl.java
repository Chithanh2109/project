package com.skincare.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.skincare.model.SkinConcern;
import com.skincare.repository.SkinConcernRepository;

@Service
public class SkinConcernRepositoryImpl implements SkinConcernRepository {
    
    /**
     * Returns all available skin concerns
     * @return A list of all SkinConcern enum values
     */
    @Override
    public List<SkinConcern> findAll() {
        return Arrays.asList(SkinConcern.values());
    }
    
    /**
     * Find a skin concern by its ID (ordinal value)
     * @param id The ID/ordinal of the skin concern
     * @return Optional containing the skin concern if found
     */
    @Override
    public Optional<SkinConcern> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        
        SkinConcern[] concerns = SkinConcern.values();
        int ordinal = id.intValue();
        
        if (ordinal >= 0 && ordinal < concerns.length) {
            return Optional.of(concerns[ordinal]);
        } else {
            return Optional.empty();
        }
    }
    
    /**
     * Find a skin concern by its name
     * @param name The name of the skin concern
     * @return The skin concern, if found
     */
    @Override
    public SkinConcern findByName(String name) {
        if (name == null) {
            return null;
        }
        
        try {
            return SkinConcern.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Find a skin concern by its description
     * @param description The description of the skin concern
     * @return The skin concern, if found
     */
    @Override
    public SkinConcern findByDescription(String description) {
        if (description == null) {
            return null;
        }
        
        return Arrays.stream(SkinConcern.values())
                .filter(concern -> concern.getDescription().equalsIgnoreCase(description))
                .findFirst()
                .orElse(null);
    }
} 