package com.skincare.repository;

import com.skincare.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") String role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.active = :active")
    long countByRoleAndActive(@Param("role") String role, @Param("active") boolean active);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER' AND u.createdAt >= :startOfMonth")
    long countNewCustomersThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth);
    
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND " +
           "(:search IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchCustomers(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND u.active = :active")
    Page<User> findByActive(@Param("active") boolean active, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND u.averageRating >= :rating")
    Page<User> findByRatingGreaterThanEqual(@Param("rating") double rating, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' AND " +
           "(:minAppointments IS NULL OR u.totalAppointments >= :minAppointments) AND " +
           "(:maxAppointments IS NULL OR u.totalAppointments <= :maxAppointments)")
    Page<User> findByAppointmentCountRange(
            @Param("minAppointments") Integer minAppointments,
            @Param("maxAppointments") Integer maxAppointments,
            Pageable pageable);
} 