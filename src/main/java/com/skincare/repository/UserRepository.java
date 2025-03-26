package com.skincare.repository;

import com.skincare.model.User;
import com.skincare.model.User.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho thao tác với dữ liệu User
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Tìm người dùng theo username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Tìm người dùng theo email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Kiểm tra username đã tồn tại chưa
     */
    boolean existsByUsername(String username);
    
    /**
     * Kiểm tra email đã tồn tại chưa
     */
    boolean existsByEmail(String email);
    
    /**
     * Tìm tất cả người dùng có vai trò cụ thể
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Tìm tất cả người dùng mà tên chứa từ khóa
     */
    List<User> findByFullNameContainingIgnoreCase(String keyword);
    
    /**
     * Tìm tất cả người dùng có vai trò và tên phù hợp
     */
    List<User> findByRoleAndFullNameContainingIgnoreCase(UserRole role, String keyword);
} 