package com.skincare.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skincare.model.Category;
import com.skincare.model.Service;
import com.skincare.model.ServiceStatus;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    List<Service> findByStatusOrderByNameAsc(String status);
    
    List<Service> findByStatusAndFeaturedTrueOrderByNameAsc(String status);
    
    List<Service> findByCategoryIdOrderByNameAsc(Long categoryId);
    
    List<Service> findByStatusAndCategoryIdOrderByNameAsc(String status, Long categoryId);
    
    List<Service> findByNameContainingIgnoreCase(String keyword);
    
    List<Service> findByDescriptionContainingIgnoreCase(String keyword);
    
    List<Service> findByPriceBetweenOrderByPriceAsc(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Service> findByDurationBetweenOrderByDurationAsc(Integer minDuration, Integer maxDuration);
    
    Optional<Service> findByName(String name);

    List<Service> findByStatus(ServiceStatus status);
    List<Service> findByCategory_Id(Long categoryId);
    List<Service> findByFeaturedTrue();
    List<Service> findByCategoryAndIdNot(Category category, Long id);

    @Query("SELECT c FROM Category c")
    List<Category> findAllCategories();

    @Query("SELECT c FROM Category c WHERE c.active = true")
    List<Category> findActiveCategories();
} 