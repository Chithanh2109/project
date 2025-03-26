package com.skincare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Model đại diện cho người dùng trong hệ thống
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "full_name", length = 100)
    private String fullName;
    
    @Column(length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CUSTOMER;
    
    private boolean enabled = true;
    
    @ElementCollection
    @CollectionTable(name = "user_skin_info", 
                    joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skin_type")
    @Enumerated(EnumType.STRING)
    private Set<SkinType> skinTypes = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "user_skin_concerns", 
                    joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skin_concern")
    @Enumerated(EnumType.STRING)
    private Set<SkinConcern> skinConcerns = new HashSet<>();
    
    @Column(length = 500)
    private String bio;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Các vai trò của người dùng trong hệ thống
     */
    public enum UserRole {
        GUEST("Khách"),
        CUSTOMER("Khách hàng"),
        THERAPIST("Chuyên viên da"),
        STAFF("Nhân viên"),
        MANAGER("Quản lý");
        
        private final String displayName;
        
        UserRole(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public User() {
    }
    
    public User(String username, String password, String email, UserRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
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
    
    public Set<SkinType> getSkinTypes() {
        return skinTypes;
    }
    
    public void setSkinTypes(Set<SkinType> skinTypes) {
        this.skinTypes = skinTypes;
    }
    
    public Set<SkinConcern> getSkinConcerns() {
        return skinConcerns;
    }
    
    public void setSkinConcerns(Set<SkinConcern> skinConcerns) {
        this.skinConcerns = skinConcerns;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Helper methods
    public void addSkinType(SkinType skinType) {
        skinTypes.add(skinType);
    }
    
    public void removeSkinType(SkinType skinType) {
        skinTypes.remove(skinType);
    }
    
    public void addSkinConcern(SkinConcern concern) {
        skinConcerns.add(concern);
    }
    
    public void removeSkinConcern(SkinConcern concern) {
        skinConcerns.remove(concern);
    }
    
    // Method to check if user has a specific role
    public boolean hasRole(UserRole role) {
        return this.role == role;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        
        return id != null ? id.equals(user.id) : user.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
} 