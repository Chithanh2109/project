package com.skincenter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "specialist_id")
    private User specialist;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private SkinCareService service;

    @Column(nullable = false)
    private LocalDateTime appointmentDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column
    private LocalDateTime checkinTime;

    @Column
    private LocalDateTime checkoutTime;

    @Column(length = 1000)
    private String notes;

    @Column
    private String treatmentResults;
} 