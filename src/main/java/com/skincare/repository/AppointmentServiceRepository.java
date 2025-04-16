package com.skincare.repository;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentService;
import com.skincare.model.Service;
import com.skincare.model.ServiceStatus;
import com.skincare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
    
    List<AppointmentService> findByAppointment(Appointment appointment);
    
    List<AppointmentService> findByService(Service service);
    
    List<AppointmentService> findByAppointmentAndStatus(Appointment appointment, ServiceStatus status);
    
    List<AppointmentService> findByPerformedBy(User therapist);
    
    List<AppointmentService> findByPerformedByAndStatusAndStartTimeBetween(
        User therapist, 
        ServiceStatus status, 
        LocalDateTime startDate, 
        LocalDateTime endDate);

    List<AppointmentService> findByAppointmentId(Long appointmentId);
    
    Optional<AppointmentService> findByAppointmentIdAndServiceId(Long appointmentId, Long serviceId);
    
    List<AppointmentService> findByPerformedById(Long therapistId);
    
    @Query("SELECT as FROM AppointmentService as WHERE as.appointment.appointmentDate BETWEEN :start AND :end")
    List<AppointmentService> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT as FROM AppointmentService as WHERE as.performedBy.id = :therapistId AND as.status = 'COMPLETED' AND as.appointment.appointmentDate BETWEEN :start AND :end")
    List<AppointmentService> findCompletedServicesByTherapistAndDateRange(Long therapistId, LocalDateTime start, LocalDateTime end);
} 