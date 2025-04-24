package com.simplebeauty.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Default constructor
    public Appointment() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with essential fields
    public Appointment(User user, Service service, LocalDateTime appointmentTime) {
        this.user = user;
        this.service = service;
        this.appointmentTime = appointmentTime;
        this.status = AppointmentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Service getService() {
        return service;
    }
    
    public void setService(Service service) {
        this.service = service;
    }
    
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", service=" + service.getId() +
                ", appointmentTime=" + appointmentTime +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
