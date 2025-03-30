package com.skincare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skincare.model.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    
    List<Blog> findByStatusOrderByCreatedAtDesc(String status);
    
    List<Blog> findByStatusAndFeaturedTrueOrderByCreatedAtDesc(String status);
    
    List<Blog> findTop3ByStatusOrderByCreatedAtDesc(String status);
} 