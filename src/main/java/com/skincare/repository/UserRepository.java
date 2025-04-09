package com.skincare.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skincare.model.User;
import com.skincare.model.UserType;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.userType = :userType")
    List<User> findByUserType(@Param("userType") UserType userType);
    
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRolesName(String roleName);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(String roleName);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_THERAPIST' AND u.active = true")
    List<User> findActiveTherapists();
} 