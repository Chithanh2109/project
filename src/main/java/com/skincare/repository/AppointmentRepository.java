package com.skincare.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.model.Customer;
import com.skincare.model.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    List<Appointment> findByCustomerOrderByAppointmentDateDesc(Customer customer);
    
    List<Appointment> findByTherapistOrderByAppointmentDateDesc(User therapist);
    
    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);
    
    List<Appointment> findByAppointmentDateAfterAndStatusNotInOrderByAppointmentDateAsc(
            LocalDateTime date, List<AppointmentStatus> statuses);
    
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDateTime start, LocalDateTime end);
    
    List<Appointment> findByCustomerAndStatusNotInAndAppointmentDateBetween(
            Customer customer, List<AppointmentStatus> statuses, LocalDateTime start, LocalDateTime end);
    
    List<Appointment> findByTherapistAndStatusNotInAndAppointmentDateBetween(
            User therapist, List<AppointmentStatus> statuses, LocalDateTime start, LocalDateTime end);
    
    long countByStatusAndAppointmentDateBetween(
            AppointmentStatus status, LocalDateTime start, LocalDateTime end);
    
    List<Appointment> findByStatusInAndAppointmentDateBetweenOrderByAppointmentDateAsc(
            List<AppointmentStatus> statuses, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate <= CURRENT_TIMESTAMP ORDER BY a.appointmentDate DESC")
    List<Appointment> findRecentAppointments(int limit);
    
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE ((a.customer = :customer AND a.status NOT IN :excludedStatuses) OR " +
           "(a.therapist = :therapist AND a.status NOT IN :excludedStatuses)) AND " +
           "a.appointmentDate BETWEEN :start AND :end AND a.id <> :excludeId")
    boolean existsConflictingAppointment(Customer customer, User therapist, List<AppointmentStatus> excludedStatuses,
                                         LocalDateTime start, LocalDateTime end, Long excludeId);
    
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
    
    List<Appointment> findByTherapistAndAppointmentDateBetweenAndStatusNotIn(
            User therapist, LocalDateTime start, LocalDateTime end, List<AppointmentStatus> statuses);
} 