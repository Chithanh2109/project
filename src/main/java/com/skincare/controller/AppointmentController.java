package com.skincare.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.service.AppointmentManagementService;
import com.skincare.service.UserService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    @Autowired
    private AppointmentManagementService appointmentService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/customer/{customerId}")
    public List<Appointment> getAppointmentsByCustomer(@PathVariable Long customerId) {
        return appointmentService.getAppointmentsByCustomer(customerId);
    }

    @GetMapping("/therapist/{therapistId}")
    public List<Appointment> getAppointmentsByTherapist(@PathVariable Long therapistId) {
        return appointmentService.getAppointmentsByTherapist(therapistId);
    }

    @GetMapping("/status/{status}")
    public List<Appointment> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    @GetMapping("/upcoming")
    public List<Appointment> getUpcomingAppointments() {
        return appointmentService.getUpcomingAppointments();
    }

    @GetMapping("/date")
    public List<Appointment> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        return appointmentService.getAppointmentsByDate(date);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentService.saveAppointment(appointment, new ArrayList<>()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        return appointmentService.getAppointmentById(id)
            .map(existingAppointment -> {
                appointmentDetails.setId(id);
                return ResponseEntity.ok(appointmentService.updateAppointment(appointmentDetails));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointment(id, "Cancelled by admin", true);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestBody String reason) {
        appointmentService.cancelAppointment(id, reason, false);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        appointmentService.confirmAppointment(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<?> checkInAppointment(@PathVariable Long id) {
        appointmentService.checkInAppointment(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<?> checkOutAppointment(@PathVariable Long id) {
        appointmentService.checkOutAppointment(id, "CASH");
        return ResponseEntity.ok().build();
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

    @PostMapping("/{id}/add-service")
    public ResponseEntity<?> addServiceToAppointment(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, Object> requestData) {
        try {
            Long serviceId = Long.parseLong(requestData.get("serviceId").toString());
            appointmentService.addServiceToAppointment(id, serviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/remove-service/{serviceId}")
    public ResponseEntity<?> removeServiceFromAppointment(
            @PathVariable Long id,
            @PathVariable Long serviceId) {
        try {
            appointmentService.removeServiceFromAppointment(id, serviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
} 