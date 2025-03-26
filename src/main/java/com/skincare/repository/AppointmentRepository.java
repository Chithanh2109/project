package com.skincare.repository;

import com.skincare.model.Appointment;
import com.skincare.model.Appointment.AppointmentStatus;
import com.skincare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository cho thao tác với dữ liệu Appointment
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    /**
     * Tìm các lịch hẹn của một khách hàng cụ thể
     */
    List<Appointment> findByClient(User client);
    
    /**
     * Tìm các lịch hẹn của một khách hàng theo trạng thái
     */
    List<Appointment> findByClientAndStatus(User client, AppointmentStatus status);
    
    /**
     * Tìm các lịch hẹn của một chuyên viên cụ thể
     */
    List<Appointment> findByTherapist(User therapist);
    
    /**
     * Tìm các lịch hẹn của một chuyên viên theo trạng thái
     */
    List<Appointment> findByTherapistAndStatus(User therapist, AppointmentStatus status);
    
    /**
     * Tìm các lịch hẹn theo khoảng thời gian
     */
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Tìm các lịch hẹn theo trạng thái và khoảng thời gian
     */
    List<Appointment> findByStatusAndAppointmentDateTimeBetween(
            AppointmentStatus status, LocalDateTime start, LocalDateTime end);
    
    /**
     * Tìm các lịch hẹn của một chuyên viên trong khoảng thời gian
     */
    List<Appointment> findByTherapistAndAppointmentDateTimeBetween(
            User therapist, LocalDateTime start, LocalDateTime end);
    
    /**
     * Đếm số lượng lịch hẹn theo trạng thái trong khoảng thời gian
     */
    @Query("SELECT COUNT(a) FROM Appointment a " +
           "WHERE a.status = ?1 AND a.appointmentDateTime BETWEEN ?2 AND ?3")
    Long countByStatusAndAppointmentDateTimeBetween(
            AppointmentStatus status, LocalDateTime start, LocalDateTime end);
    
    /**
     * Tìm các lịch hẹn gần đây sắp xếp theo thời gian
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.appointmentDateTime >= ?1 " +
           "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointments(LocalDateTime from);
} 