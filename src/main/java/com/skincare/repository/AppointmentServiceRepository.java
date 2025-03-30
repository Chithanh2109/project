package com.skincare.repository;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentService;
import com.skincare.model.Service;
import com.skincare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
    
    List<AppointmentService> findByAppointment(Appointment appointment);
    
    List<AppointmentService> findByService(Service service);
    
    List<AppointmentService> findByAppointmentAndStatus(Appointment appointment, AppointmentService.ServiceStatus status);
    
    List<AppointmentService> findByPerformedBy(User therapist);
    
    List<AppointmentService> findByPerformedByAndStatusAndStartTimeBetween(
        User therapist, 
        AppointmentService.ServiceStatus status, 
        LocalDateTime startDate, 
        LocalDateTime endDate);
} 