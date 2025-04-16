package com.skincare.service;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentReportService {

    private final AppointmentRepository appointmentRepository;

    public Map<String, Object> getDailyStatistics(LocalDate date) {
        Map<String, Object> statistics = new HashMap<>();
        
        // Lấy tất cả lịch hẹn trong ngày
        List<Appointment> appointments = appointmentRepository.findByDate(date);
        
        // Thống kê theo trạng thái
        Map<AppointmentStatus, Long> statusCount = new HashMap<>();
        for (AppointmentStatus status : AppointmentStatus.values()) {
            statusCount.put(status, appointments.stream()
                    .filter(a -> a.getStatus() == status)
                    .count());
        }
        
        // Thống kê theo giờ
        Map<Integer, Long> hourlyCount = new HashMap<>();
        for (int hour = 8; hour <= 20; hour++) {
            LocalTime startTime = LocalTime.of(hour, 0);
            LocalTime endTime = LocalTime.of(hour + 1, 0);
            hourlyCount.put(hour, appointments.stream()
                    .filter(a -> a.getTime().isAfter(startTime) && a.getTime().isBefore(endTime))
                    .count());
        }
        
        statistics.put("totalAppointments", appointments.size());
        statistics.put("statusCount", statusCount);
        statistics.put("hourlyCount", hourlyCount);
        
        return statistics;
    }

    public Map<String, Object> getMonthlyStatistics(int year, int month) {
        Map<String, Object> statistics = new HashMap<>();
        
        // Lấy tất cả lịch hẹn trong tháng
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateTimeBetween(startOfMonth, endOfMonth);
        
        // Thống kê theo trạng thái
        Map<AppointmentStatus, Long> statusCount = new HashMap<>();
        for (AppointmentStatus status : AppointmentStatus.values()) {
            statusCount.put(status, appointments.stream()
                    .filter(a -> a.getStatus() == status)
                    .count());
        }
        
        // Thống kê theo ngày
        Map<Integer, Long> dailyCount = new HashMap<>();
        for (int day = 1; day <= 31; day++) {
            try {
                LocalDate date = LocalDate.of(year, month, day);
                dailyCount.put(day, appointments.stream()
                        .filter(a -> a.getDate().equals(date))
                        .count());
            } catch (Exception e) {
                // Bỏ qua các ngày không hợp lệ
            }
        }
        
        statistics.put("totalAppointments", appointments.size());
        statistics.put("statusCount", statusCount);
        statistics.put("dailyCount", dailyCount);
        
        return statistics;
    }

    public Map<String, Object> getServiceStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        // Lấy tất cả lịch hẹn trong khoảng thời gian
        List<Appointment> appointments = appointmentRepository.findByDateBetween(startDate, endDate);
        
        // Thống kê theo dịch vụ
        Map<String, Long> serviceCount = new HashMap<>();
        appointments.forEach(appointment -> {
            String serviceName = appointment.getService().getName();
            serviceCount.merge(serviceName, 1L, Long::sum);
        });
        
        // Thống kê theo chuyên viên
        Map<String, Long> specialistCount = new HashMap<>();
        appointments.forEach(appointment -> {
            if (appointment.getSpecialist() != null) {
                String specialistName = appointment.getSpecialist().getFullName();
                specialistCount.merge(specialistName, 1L, Long::sum);
            }
        });
        
        statistics.put("totalAppointments", appointments.size());
        statistics.put("serviceCount", serviceCount);
        statistics.put("specialistCount", specialistCount);
        
        return statistics;
    }

    public Map<String, Object> getCustomerStatistics(Long customerId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // Lấy tất cả lịch hẹn của khách hàng
        List<Appointment> appointments = appointmentRepository.findByCustomerOrderByDateDesc(customerId);
        
        // Thống kê theo trạng thái
        Map<AppointmentStatus, Long> statusCount = new HashMap<>();
        for (AppointmentStatus status : AppointmentStatus.values()) {
            statusCount.put(status, appointments.stream()
                    .filter(a -> a.getStatus() == status)
                    .count());
        }
        
        // Thống kê theo dịch vụ
        Map<String, Long> serviceCount = new HashMap<>();
        appointments.forEach(appointment -> {
            String serviceName = appointment.getService().getName();
            serviceCount.merge(serviceName, 1L, Long::sum);
        });
        
        statistics.put("totalAppointments", appointments.size());
        statistics.put("statusCount", statusCount);
        statistics.put("serviceCount", serviceCount);
        
        return statistics;
    }
} 