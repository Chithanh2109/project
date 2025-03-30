package com.skincare.controller;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentService;
import com.skincare.model.Appointment.AppointmentStatus;
import com.skincare.service.AppointmentService;
import com.skincare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    private final UserService userService;
    
    @Autowired
    public AppointmentController(AppointmentService appointmentService, UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByCustomer(@PathVariable Long customerId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(customerId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/therapist/{therapistId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByTherapist(@PathVariable Long therapistId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByTherapist(therapistId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<Appointment>> getUpcomingAppointments() {
        List<Appointment> appointments = appointmentService.getUpcomingAppointments();
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/date")
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Appointment appointment) {
        try {
            Appointment savedAppointment = appointmentService.saveAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
        try {
            Optional<Appointment> existingAppointment = appointmentService.getAppointmentById(id);
            
            if (existingAppointment.isPresent()) {
                appointment.setId(id);
                Appointment updatedAppointment = appointmentService.saveAppointment(appointment);
                return ResponseEntity.ok(updatedAppointment);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        try {
            appointmentService.cancelAppointment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/checkin")
    public ResponseEntity<?> checkInAppointment(@PathVariable Long id) {
        try {
            appointmentService.checkInAppointment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/checkout")
    public ResponseEntity<?> checkOutAppointment(@PathVariable Long id) {
        try {
            appointmentService.checkOutAppointment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/assign/{therapistId}")
    public ResponseEntity<?> assignTherapist(@PathVariable Long id, @PathVariable Long therapistId) {
        try {
            appointmentService.assignTherapist(id, therapistId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
} 