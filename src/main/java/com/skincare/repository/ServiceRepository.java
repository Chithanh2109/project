package com.skincare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skincare.model.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    List<Service> findByStatusOrderByNameAsc(String status);
    
    List<Service> findByStatusAndFeaturedTrueOrderByNameAsc(String status);
    
    List<Service> findByCategoryIdAndStatusOrderByNameAsc(Long categoryId, String status);
    
    @Query("SELECT s FROM Service s WHERE s.category.id = ?1 AND s.id != ?2 AND s.status = 'ACTIVE'")
    List<Service> findRelatedServices(Long categoryId, Long currentServiceId);
    
    List<Service> findByNameContainingIgnoreCaseAndStatusOrderByNameAsc(String keyword, String status);

    List<Service> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);

    List<Service> findByActiveTrue();

    List<Service> findByCategoryId(Long categoryId);

    List<Service> findBySkinConcernsIdIn(List<Long> concernIds);
} 