package com.skincenter.model;

public enum AppointmentStatus {
    PENDING,       // Initial state after booking
    CONFIRMED,     // Confirmed by staff
    CHECKED_IN,    // Customer has arrived
    IN_PROGRESS,   // Service is being performed
    COMPLETED,     // Service has been completed
    CHECKED_OUT,   // Customer has left
    CANCELLED,     // Cancelled by customer or staff
    NO_SHOW        // Customer didn't show up
} 