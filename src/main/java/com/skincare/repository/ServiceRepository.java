package com.skincare.repository;

import com.skincare.model.Category;
import com.skincare.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    List<Service> findByActiveTrue();
    
    List<Service> findByCategoryAndActiveTrue(Category category);
    
    List<Service> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);
    
    List<Service> findByPriceLessThanEqualAndActiveTrue(double maxPrice);
    
    List<Service> findByPriceBetweenAndActiveTrue(double minPrice, double maxPrice);
    
    List<Service> findByDurationMinutesLessThanEqualAndActiveTrue(int maxDuration);
} 