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
@Table(name = "ratings")
public class Rating {

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
    @JoinColumn(name = "service_id")
    private SkinCareService service;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(nullable = false)
    private Integer ratingValue; // 1-5 stars

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime ratingDate = LocalDateTime.now();

    @Column
    private Boolean isPublic = true;
} 