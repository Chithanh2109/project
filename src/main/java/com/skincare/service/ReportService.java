package com.skincare.service;

import com.skincare.dto.DashboardStats;
import com.skincare.dto.ServiceStat;
import com.skincare.dto.TherapistStat;
import com.skincare.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportService {
    
    /**
     * Lấy thống kê tổng hợp cho dashboard
     */
    DashboardStats getDashboardStats(LocalDate start, LocalDate end);
    
    /**
     * Lấy dãy ngày từ ngày bắt đầu đến ngày kết thúc
     */
    List<LocalDate> getDateRange(LocalDate start, LocalDate end);
    
    /**
     * Lấy số lượng lịch hẹn theo ngày
     */
    List<Long> getAppointmentCountsByDate(LocalDate start, LocalDate end);
    
    /**
     * Lấy số lượng lịch hẹn theo trạng thái
     */
    long getAppointmentCountByStatus(AppointmentStatus status, LocalDate start, LocalDate end);
    
    /**
     * Lấy danh sách dịch vụ phổ biến nhất
     */
    List<ServiceStat> getPopularServices(LocalDate start, LocalDate end, int limit);
    
    /**
     * Lấy danh sách chuyên viên xuất sắc nhất
     */
    List<TherapistStat> getTopTherapists(LocalDate start, LocalDate end, int limit);
    
    /**
     * Lấy doanh thu theo ngày
     */
    Map<LocalDate, Double> getRevenueByDate(LocalDate start, LocalDate end);
    
    /**
     * Lấy doanh thu theo thời gian trong ngày (buổi sáng, chiều, tối)
     */
    Map<String, Double> getRevenueByTimeOfDay(LocalDate start, LocalDate end);
    
    /**
     * Lấy doanh thu theo dịch vụ
     */
    Map<String, Double> getRevenueByService(LocalDate start, LocalDate end);
} 