package com.skincare.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Đối tượng User đại diện cho người dùng trong hệ thống
 */
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phone;
    private UserRole role;
    private boolean enabled = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Enum định nghĩa các vai trò của người dùng trong hệ thống
     */
    public enum UserRole {
        GUEST,       // Khách vãng lai
        CUSTOMER,    // Khách hàng đã đăng ký
        THERAPIST,   // Chuyên viên trị liệu da
        STAFF,       // Nhân viên tại trung tâm
        MANAGER      // Quản lý trung tâm
    }

    public User() {
        // Default constructor
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    public User(String username, String email, String password, UserRole role) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 