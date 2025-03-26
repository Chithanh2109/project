package com.skincare.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Model đại diện cho các dịch vụ chăm sóc da trong hệ thống
 */
@Entity
@Table(name = "skin_services")
public class SkinCareService {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "service_category")
    private ServiceCategory category;
    
    @ElementCollection
    @CollectionTable(name = "service_skin_types", 
                    joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "skin_type")
    @Enumerated(EnumType.STRING)
    private Set<SkinType> suitableSkinTypes = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "service_skin_concerns", 
                    joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "skin_concern")
    @Enumerated(EnumType.STRING)
    private Set<SkinConcern> targetSkinConcerns = new HashSet<>();
    
    private boolean active = true;
    
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
    
    // Enum cho các loại dịch vụ
    public enum ServiceCategory {
        FACIAL_TREATMENT("Điều trị da mặt"),
        SKIN_CONSULTATION("Tư vấn da"),
        ACNE_TREATMENT("Điều trị mụn"),
        ANTI_AGING("Chống lão hóa"),
        BRIGHTENING("Làm sáng da"),
        MOISTURIZING("Dưỡng ẩm"),
        DEEP_CLEANSING("Làm sạch sâu"),
        SPECIAL_CARE("Chăm sóc đặc biệt");
        
        private final String displayName;
        
        ServiceCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public SkinCareService() {
    }
    
    public SkinCareService(String name, String description, BigDecimal price, 
                        Integer durationMinutes, ServiceCategory category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public ServiceCategory getCategory() {
        return category;
    }
    
    public void setCategory(ServiceCategory category) {
        this.category = category;
    }
    
    public Set<SkinType> getSuitableSkinTypes() {
        return suitableSkinTypes;
    }
    
    public void setSuitableSkinTypes(Set<SkinType> suitableSkinTypes) {
        this.suitableSkinTypes = suitableSkinTypes;
    }
    
    public Set<SkinConcern> getTargetSkinConcerns() {
        return targetSkinConcerns;
    }
    
    public void setTargetSkinConcerns(Set<SkinConcern> targetSkinConcerns) {
        this.targetSkinConcerns = targetSkinConcerns;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Helper methods to add and remove skin types and concerns
    public void addSkinType(SkinType skinType) {
        suitableSkinTypes.add(skinType);
    }
    
    public void removeSkinType(SkinType skinType) {
        suitableSkinTypes.remove(skinType);
    }
    
    public void addSkinConcern(SkinConcern concern) {
        targetSkinConcerns.add(concern);
    }
    
    public void removeSkinConcern(SkinConcern concern) {
        targetSkinConcerns.remove(concern);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        SkinCareService service = (SkinCareService) o;
        
        return id != null ? id.equals(service.id) : service.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "SkinCareService{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", active=" + active +
                '}';
    }
} 