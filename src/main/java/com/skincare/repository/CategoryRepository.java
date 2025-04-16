package com.skincare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skincare.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByActiveTrue();
    
    List<Category> findByActiveTrueOrderByDisplayOrderAsc();
    
    Optional<Category> findByNameIgnoreCase(String name);
    
    List<Category> findByNameContainingIgnoreCase(String keyword);

    List<Category> findByStatusOrderByNameAsc(String status);
} 