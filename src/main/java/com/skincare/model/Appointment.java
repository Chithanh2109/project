package com.skincare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Model quản lý lịch hẹn sử dụng dịch vụ chăm sóc da
 */
@Entity
@Table(name = "appointments")
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id")
    private User therapist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private SkinCareService service;
    
    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime appointmentDateTime;
    
    @Column(name = "end_datetime")
    private LocalDateTime endDateTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;
    
    @Column(name = "special_requests", length = 500)
    private String specialRequests;
    
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
     * Trạng thái của lịch hẹn
     */
    public enum AppointmentStatus {
        PENDING("Chờ xác nhận"),
        CONFIRMED("Đã xác nhận"),
        COMPLETED("Đã hoàn thành"),
        CANCELLED("Đã hủy"),
        NO_SHOW("Không đến");
        
        private final String displayName;
        
        AppointmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public Appointment() {
    }
    
    public Appointment(User client, SkinCareService service, LocalDateTime appointmentDateTime) {
        this.client = client;
        this.service = service;
        this.appointmentDateTime = appointmentDateTime;
        if (service.getDurationMinutes() != null) {
            this.endDateTime = appointmentDateTime.plusMinutes(service.getDurationMinutes());
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getClient() {
        return client;
    }
    
    public void setClient(User client) {
        this.client = client;
    }
    
    public User getTherapist() {
        return therapist;
    }
    
    public void setTherapist(User therapist) {
        this.therapist = therapist;
    }
    
    public SkinCareService getService() {
        return service;
    }
    
    public void setService(SkinCareService service) {
        this.service = service;
        if (service.getDurationMinutes() != null && this.appointmentDateTime != null) {
            this.endDateTime = appointmentDateTime.plusMinutes(service.getDurationMinutes());
        }
    }
    
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }
    
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
        if (service != null && service.getDurationMinutes() != null) {
            this.endDateTime = appointmentDateTime.plusMinutes(service.getDurationMinutes());
        }
    }
    
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
    
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Appointment that = (Appointment) o;
        
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", client=" + (client != null ? client.getUsername() : "none") +
                ", service=" + (service != null ? service.getName() : "none") +
                ", appointmentDateTime=" + appointmentDateTime +
                ", status=" + status +
                '}';
    }
} 