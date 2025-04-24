package com.simplebeauty.model;

/**
 * Enum representing the possible statuses of an appointment.
 */
public enum AppointmentStatus {
    PENDING,        // Appointment is requested but not confirmed
    CONFIRMED,      // Appointment is confirmed by staff
    COMPLETED,      // Appointment has been completed
    CANCELLED,      // Appointment was cancelled by customer
    NO_SHOW,        // Customer didn't show up
    RESCHEDULED     // Appointment was rescheduled
} 