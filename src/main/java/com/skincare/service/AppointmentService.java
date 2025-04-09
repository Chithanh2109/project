package com.skincare.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    Optional<Appointment> getAppointmentById(Long id);
    Appointment createAppointment(Appointment appointment);
    Appointment updateAppointment(Long id, Appointment appointment);
    void deleteAppointment(Long id);
    List<Appointment> getAppointmentsByCustomerId(Long customerId);
    List<Appointment> getAppointmentsByServiceId(Long serviceId);
    List<Appointment> getUpcomingAppointments();
    List<Appointment> getAppointmentsByStatus(AppointmentStatus status);
    List<Appointment> getAppointmentsByDate(LocalDate date);
    void checkInAppointment(Long id);
    void checkOutAppointment(Long id);
    void assignTherapist(Long appointmentId, Long therapistId);
    void cancelAppointment(Long id);
    List<Appointment> getAppointmentsByCustomer(Long customerId);
} 