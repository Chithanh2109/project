package com.simplebeauty.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "services")
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String shortDescription;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer durationMinutes;
    
    private String imageUrl;
    
    @Column(unique = true, nullable = false)
    private String slug;
    
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private Set<Appointment> appointments = new HashSet<>();
    
    // Default constructor
    public Service() {
    }
    
    // Constructor with essential fields
    public Service(String name, String shortDescription, BigDecimal price, Integer durationMinutes, String slug) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.slug = slug;
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
    
    public String getShortDescription() {
        return shortDescription;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public Set<Appointment> getAppointments() {
        return appointments;
    }
    
    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
    
    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", price=" + price +
                ", durationMinutes=" + durationMinutes +
                ", slug='" + slug + '\'' +
                '}';
    }
}
