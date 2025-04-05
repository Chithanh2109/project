package com.skincare.model;

public enum AppointmentStatus {
    PENDING,       // Initial state after booking
    CONFIRMED,     // Confirmed by staff
    CHECKED_IN,    // Customer has arrived
    IN_PROGRESS,   // Service is being performed
    COMPLETED,     // Service has been completed
    CHECKED_OUT,   // Customer has left and paid
    CANCELLED,     // Cancelled by customer or staff
    NO_SHOW        // Customer didn't show up
} 