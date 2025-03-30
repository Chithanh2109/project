package com.skincare.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "specialist_id")
    private User specialist;

    @ManyToMany
    @JoinTable(
        name = "appointment_services",
        joinColumns = @JoinColumn(name = "appointment_id"),
        inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> services;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    private String notes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    private LocalDateTime createdAt;
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
} 