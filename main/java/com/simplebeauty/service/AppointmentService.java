package com.simplebeauty.service;

import com.simplebeauty.model.Appointment;
import com.simplebeauty.model.AppointmentStatus;
import com.simplebeauty.model.Service;
import com.simplebeauty.model.User;
import com.simplebeauty.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final ServiceService serviceService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, UserService userService, ServiceService serviceService) {
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
        this.serviceService = serviceService;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByUser(User user) {
        return appointmentRepository.findByUserOrderByAppointmentTimeDesc(user);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn với id: " + id));
    }

    public Appointment createAppointment(Long userId, Long serviceId, LocalDateTime appointmentTime, String notes) {
        User user = userService.findById(userId);
        Service service = serviceService.getServiceById(serviceId);
        
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setService(service);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setNotes(notes);
        appointment.setStatus(AppointmentStatus.PENDING);
        
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> getUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.plusDays(7);
        return appointmentRepository.findByAppointmentTimeBetween(now, endOfDay);
    }
}
