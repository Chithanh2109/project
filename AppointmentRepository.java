package com.skincare.repository;

import com.skincare.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    boolean existsByCustomerId(Long customerId);
    
    @Query("SELECT AVG(a.totalAmount) FROM Appointment a WHERE a.customer.id = :customerId")
    Double getAverageAppointmentAmount(@Param("customerId") Long customerId);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.customer.id = :customerId")
    Integer getTotalAppointments(@Param("customerId") Long customerId);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId ORDER BY a.appointmentDateTime DESC")
    Page<Appointment> findByCustomerIdOrderByAppointmentDateTimeDesc(
            @Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'COMPLETED'")
    List<Appointment> findCompletedAppointmentsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'PENDING'")
    List<Appointment> findPendingAppointmentsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'CANCELLED'")
    List<Appointment> findCancelledAppointmentsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'COMPLETED' " +
           "ORDER BY a.appointmentDateTime DESC")
    Page<Appointment> findCompletedAppointmentsByCustomerIdOrderByDateTimeDesc(
            @Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'PENDING' " +
           "ORDER BY a.appointmentDateTime ASC")
    Page<Appointment> findPendingAppointmentsByCustomerIdOrderByDateTimeAsc(
            @Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'CANCELLED' " +
           "ORDER BY a.appointmentDateTime DESC")
    Page<Appointment> findCancelledAppointmentsByCustomerIdOrderByDateTimeDesc(
            @Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'COMPLETED' " +
           "AND a.appointmentDateTime BETWEEN :startDate AND :endDate")
    List<Appointment> findCompletedAppointmentsByCustomerIdAndDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'COMPLETED'")
    Integer getCompletedAppointmentsCount(@Param("customerId") Long customerId);
    
    @Query("SELECT SUM(a.totalAmount) FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'COMPLETED'")
    Double getTotalSpentByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'COMPLETED' " +
           "ORDER BY a.appointmentDateTime DESC LIMIT 1")
    Appointment getLastCompletedAppointment(@Param("customerId") Long customerId);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'COMPLETED' " +
           "ORDER BY a.appointmentDateTime DESC")
    List<Appointment> getLastCompletedAppointments(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId AND a.status = 'COMPLETED' " +
           "ORDER BY a.appointmentDateTime DESC")
    Page<Map<String, Object>> findCustomerHistory(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT a FROM Appointment a WHERE a.customer.id = :customerId ORDER BY a.appointmentDateTime DESC")
    Page<Map<String, Object>> findCustomerAppointments(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT AVG(a.totalAmount) FROM Appointment a WHERE a.status = 'COMPLETED'")
    Double getAverageAppointmentsPerCustomer();
} 