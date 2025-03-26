package com.skincare.service;

import com.skincare.model.Appointment;
import com.skincare.model.Appointment.AppointmentStatus;
import com.skincare.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface cho việc quản lý lịch hẹn
 */
public interface AppointmentService {
    
    /**
     * Lấy tất cả lịch hẹn
     */
    List<Appointment> getAllAppointments();
    
    /**
     * Lấy lịch hẹn theo ID
     */
    Optional<Appointment> getAppointmentById(Long id);
    
    /**
     * Đặt lịch hẹn mới
     */
    Appointment createAppointment(Appointment appointment);
    
    /**
     * Cập nhật lịch hẹn
     */
    Appointment updateAppointment(Appointment appointment);
    
    /**
     * Hủy lịch hẹn
     */
    Appointment cancelAppointment(Long id, String reason);
    
    /**
     * Xác nhận lịch hẹn
     */
    Appointment confirmAppointment(Long id);
    
    /**
     * Đánh dấu lịch hẹn đã hoàn thành
     */
    Appointment completeAppointment(Long id);
    
    /**
     * Lấy tất cả lịch hẹn của một khách hàng
     */
    List<Appointment> getAppointmentsByClient(User client);
    
    /**
     * Lấy tất cả lịch hẹn của một chuyên viên
     */
    List<Appointment> getAppointmentsByTherapist(User therapist);
    
    /**
     * Lấy lịch hẹn theo trạng thái
     */
    List<Appointment> getAppointmentsByStatus(AppointmentStatus status);
    
    /**
     * Lấy lịch hẹn của một ngày cụ thể
     */
    List<Appointment> getAppointmentsByDate(LocalDate date);
    
    /**
     * Lấy lịch hẹn trong khoảng thời gian
     */
    List<Appointment> getAppointmentsBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Lấy lịch hẹn của chuyên viên trong khoảng thời gian
     */
    List<Appointment> getTherapistAppointmentsBetween(User therapist, LocalDateTime start, LocalDateTime end);
    
    /**
     * Kiểm tra xem thời gian có hợp lệ để đặt lịch không
     */
    boolean isTimeSlotAvailable(LocalDateTime dateTime, Long serviceId, Long therapistId);
    
    /**
     * Gán chuyên viên cho lịch hẹn
     */
    Appointment assignTherapist(Long appointmentId, Long therapistId);
    
    /**
     * Đổi lịch hẹn sang thời gian khác
     */
    Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime);
    
    /**
     * Lấy các khung giờ trống của một chuyên viên trong một ngày
     */
    List<LocalDateTime> getAvailableTimeSlots(Long therapistId, LocalDate date);
    
    /**
     * Gửi nhắc nhở cho lịch hẹn sắp tới
     */
    void sendAppointmentReminders();
    
    /**
     * Xóa lịch hẹn
     */
    void deleteAppointment(Long id);
} 