package com.skincare.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "phone")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType = UserType.CUSTOMER;
    
    @Column(name = "enabled")
    private boolean enabled = true;
    
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
        ADMIN, THERAPIST, CUSTOMER
    }
    
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
    
    public UserType getUserType() {
        return userType;
    }
    
    public void setUserType(UserType userType) {
        this.userType = userType;
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
    
    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }
    
    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }
    
    public Set<Appointment> getCustomerAppointments() {
        return customerAppointments;
    }
    
    public void setCustomerAppointments(Set<Appointment> customerAppointments) {
        this.customerAppointments = customerAppointments;
    }
    
    public Set<Appointment> getTherapistAppointments() {
        return therapistAppointments;
    }
    
    public void setTherapistAppointments(Set<Appointment> therapistAppointments) {
        this.therapistAppointments = therapistAppointments;
    }
    
    public Set<Appointment> getAssignedAppointments() {
        return assignedAppointments;
    }
    
    public void setAssignedAppointments(Set<Appointment> assignedAppointments) {
        this.assignedAppointments = assignedAppointments;
    }
    
    public Set<AppointmentService> getPerformedServices() {
        return performedServices;
    }
    
    public void setPerformedServices(Set<AppointmentService> performedServices) {
        this.performedServices = performedServices;
    }
} 