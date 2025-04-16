package com.skincare.repository;

import com.skincare.model.SkinConcern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkinConcernRepository extends JpaRepository<SkinConcern, Long> {
} 