package com.skincare.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;
    
    @Column(length = 15)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CustomerProfile customerProfile;
    
    @OneToMany(mappedBy = "customer")
    private Set<Appointment> customerAppointments = new HashSet<>();
    
    @OneToMany(mappedBy = "therapist")
    private Set<Appointment> therapistAppointments = new HashSet<>();
    
    @OneToMany(mappedBy = "assignedBy")
    private Set<Appointment> assignedAppointments = new HashSet<>();
    
    @OneToMany(mappedBy = "performedBy")
    private Set<AppointmentService> performedServices = new HashSet<>();
    
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
    
    public enum UserType {
        CUSTOMER,
        STAFF,
        SKIN_THERAPIST,
        MANAGER
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 