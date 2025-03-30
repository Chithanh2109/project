package com.skincare.repository;

import com.skincare.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByActiveTrue();
    
    Optional<Category> findByNameIgnoreCase(String name);
    
    List<Category> findByNameContainingIgnoreCase(String keyword);
} 