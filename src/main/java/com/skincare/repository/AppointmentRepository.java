package com.skincare.repository;

import com.skincare.model.Appointment;
import com.skincare.model.Appointment.AppointmentStatus;
import com.skincare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByCustomerOrderByAppointmentDateDesc(User customer);
    
    List<Appointment> findByTherapistOrderByAppointmentDateDesc(User therapist);
    
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);
    
    List<Appointment> findByAppointmentDateAfterAndStatusNotOrderByAppointmentDateAsc(
            LocalDateTime date, AppointmentStatus status);
    
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE a.customer.id = :customerId " +
           "AND a.appointmentDate BETWEEN :startTime AND :endTime " +
           "AND a.status != com.skincare.model.Appointment.AppointmentStatus.CANCELLED")
    boolean existsCustomerScheduleConflict(
            @Param("customerId") Long customerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    default boolean existsCustomerScheduleConflict(Long customerId, LocalDateTime appointmentTime) {
        // Xung đột nếu lịch hẹn cách nhau dưới 1 giờ
        LocalDateTime startTime = appointmentTime.minusHours(1);
        LocalDateTime endTime = appointmentTime.plusHours(1);
        
        return existsCustomerScheduleConflict(customerId, startTime, endTime);
    }
    
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE a.therapist.id = :therapistId " +
           "AND a.appointmentDate BETWEEN :startTime AND :endTime " +
           "AND a.status != com.skincare.model.Appointment.AppointmentStatus.CANCELLED " +
           "AND (:appointmentId IS NULL OR a.id != :appointmentId)")
    boolean existsTherapistScheduleConflict(
            @Param("therapistId") Long therapistId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("appointmentId") Long appointmentId);
    
    default boolean existsTherapistScheduleConflict(Long therapistId, LocalDateTime appointmentTime, Long appointmentId) {
        // Xung đột nếu lịch hẹn cách nhau dưới 1 giờ
        LocalDateTime startTime = appointmentTime.minusHours(1);
        LocalDateTime endTime = appointmentTime.plusHours(1);
        
        return existsTherapistScheduleConflict(therapistId, startTime, endTime, appointmentId);
    }
} 