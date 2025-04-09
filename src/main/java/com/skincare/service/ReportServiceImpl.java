package com.skincare.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skincare.dto.DashboardStats;
import com.skincare.dto.ServiceStat;
import com.skincare.dto.TherapistStat;
import com.skincare.model.Appointment;
import com.skincare.model.AppointmentService;
import com.skincare.model.AppointmentStatus;
import com.skincare.model.ServiceStatus;
import com.skincare.model.User;
import com.skincare.repository.AppointmentRepository;
import com.skincare.repository.AppointmentServiceRepository;
import com.skincare.repository.UserRepository;

@Service
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public ReportServiceImpl(
            AppointmentRepository appointmentRepository,
            AppointmentServiceRepository appointmentServiceRepository,
            UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public DashboardStats getDashboardStats(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        // Lấy tất cả lịch hẹn trong khoảng thời gian
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                startDateTime, endDateTime);
        
        // Thống kê cơ bản
        long totalAppointments = appointments.size();
        long totalCustomers = userRepository.countByRolesName("ROLE_CUSTOMER");
        long totalTherapists = userRepository.countByRolesName("ROLE_THERAPIST");
        
        // Thống kê theo trạng thái
        long completedAppointments = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED || 
                         a.getStatus() == AppointmentStatus.CHECKED_OUT)
                .count();
        
        long cancelledAppointments = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED || 
                         a.getStatus() == AppointmentStatus.NO_SHOW)
                .count();
        
        // Tính tỉ lệ hoàn thành
        double completionRate = totalAppointments > 0 ? 
                (completedAppointments * 100.0) / totalAppointments : 0;
        // Làm tròn đến 1 chữ số thập phân
        completionRate = Math.round(completionRate * 10) / 10.0;
        
        // Tính doanh thu
        BigDecimal revenue = appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED || 
                         a.getStatus() == AppointmentStatus.CHECKED_OUT)
                .map(Appointment::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Tính tỷ lệ tăng trưởng
        double growthRate = 0;
        
        // Nếu khoảng thời gian > 30 ngày, có thể tính tỷ lệ tăng trưởng so với kỳ trước
        if (start.until(end).getDays() >= 30) {
            LocalDate previousPeriodStart = start.minusDays(start.until(end).getDays());
            LocalDate previousPeriodEnd = start.minusDays(1);
            
            BigDecimal previousRevenue = appointmentRepository
                    .findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                            previousPeriodStart.atStartOfDay(), 
                            previousPeriodEnd.atTime(LocalTime.MAX))
                    .stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED || 
                             a.getStatus() == AppointmentStatus.CHECKED_OUT)
                    .map(Appointment::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (previousRevenue.compareTo(BigDecimal.ZERO) > 0) {
                growthRate = revenue.subtract(previousRevenue)
                        .multiply(new BigDecimal("100"))
                        .divide(previousRevenue, 1, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();
            }
        }
        
        return new DashboardStats(
                totalAppointments,
                totalCustomers,
                totalTherapists,
                completedAppointments,
                cancelledAppointments,
                completionRate,
                revenue,
                growthRate
        );
    }
    
    @Override
    public List<LocalDate> getDateRange(LocalDate start, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate date = start;
        
        while (!date.isAfter(end)) {
            dates.add(date);
            date = date.plusDays(1);
        }
        
        return dates;
    }
    
    @Override
    public List<Long> getAppointmentCountsByDate(LocalDate start, LocalDate end) {
        List<LocalDate> dates = getDateRange(start, end);
        List<Long> counts = new ArrayList<>();
        
        // Lấy tất cả lịch hẹn trong khoảng thời gian
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                start.atStartOfDay(), end.atTime(LocalTime.MAX));
        
        // Nhóm theo ngày
        Map<LocalDate, Long> countsByDate = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAppointmentDate().toLocalDate(),
                        Collectors.counting()
                ));
        
        // Tạo danh sách số lượng theo từng ngày
        for (LocalDate date : dates) {
            counts.add(countsByDate.getOrDefault(date, 0L));
        }
        
        return counts;
    }
    
    @Override
    public long getAppointmentCountByStatus(AppointmentStatus status, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        return appointmentRepository.countByStatusAndAppointmentDateBetween(
                status, startDateTime, endDateTime);
    }
    
    @Override
    public List<ServiceStat> getPopularServices(LocalDate start, LocalDate end, int limit) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        // Lấy tất cả dịch vụ đã đặt trong khoảng thời gian
        List<AppointmentService> services = appointmentServiceRepository.findByAppointmentDateBetween(
                startDateTime, endDateTime);
        
        // Nhóm dịch vụ theo tên và tính toán số lượng đặt và doanh thu
        Map<com.skincare.model.Service, List<AppointmentService>> serviceGroups = services.stream()
                .filter(as -> as.getAppointment().getStatus() != AppointmentStatus.CANCELLED &&
                         as.getAppointment().getStatus() != AppointmentStatus.NO_SHOW)
                .collect(Collectors.groupingBy(AppointmentService::getService));
        
        // Chuyển đổi thành danh sách ServiceStat
        List<ServiceStat> stats = serviceGroups.entrySet().stream()
                .map(entry -> {
                    com.skincare.model.Service service = entry.getKey();
                    List<AppointmentService> serviceList = entry.getValue();
                    
                    int bookingCount = serviceList.size();
                    BigDecimal revenue = serviceList.stream()
                            .map(AppointmentService::getPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return new ServiceStat(
                            service.getId(),
                            service.getName(),
                            bookingCount,
                            revenue
                    );
                })
                .sorted(Comparator.comparing(ServiceStat::getBookingCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        
        return stats;
    }
    
    @Override
    public List<TherapistStat> getTopTherapists(LocalDate start, LocalDate end, int limit) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        // Lấy tất cả dịch vụ đã hoàn thành trong khoảng thời gian
        List<AppointmentService> completedServicesList = appointmentServiceRepository.findByAppointmentDateBetween(
                startDateTime, endDateTime)
                .stream()
                .filter(as -> as.getStatus() == ServiceStatus.COMPLETED)
                .collect(Collectors.toList());
        
        // Nhóm dịch vụ theo chuyên viên 
        Map<User, List<AppointmentService>> therapistGroups = completedServicesList.stream()
                .filter(as -> as.getPerformedBy() != null)
                .collect(Collectors.groupingBy(AppointmentService::getPerformedBy));
        
        // Chuyển đổi thành danh sách TherapistStat
        List<TherapistStat> stats = therapistGroups.entrySet().stream()
                .map(entry -> {
                    User therapist = entry.getKey();
                    List<AppointmentService> serviceList = entry.getValue();
                    
                    // Tính số lịch hẹn đã phục vụ
                    Set<Long> appointmentIds = serviceList.stream()
                            .map(as -> as.getAppointment().getId())
                            .collect(Collectors.toSet());
                    
                    int appointmentCount = appointmentIds.size();
                    int completedServices = serviceList.size();
                    
                    // Tính điểm đánh giá trung bình
                    // Giả định: đánh giá được lưu trong bảng ratings và liên kết với appointment_id
                    double averageRating = 4.5; // Giá trị mặc định nếu chưa có hệ thống đánh giá
                    
                    return new TherapistStat(
                            therapist.getId(),
                            therapist.getFullName(),
                            appointmentCount,
                            averageRating,
                            completedServices
                    );
                })
                .sorted(Comparator.comparing(TherapistStat::getAppointmentCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        
        return stats;
    }
    
    @Override
    public Map<LocalDate, Double> getRevenueByDate(LocalDate start, LocalDate end) {
        List<LocalDate> dates = getDateRange(start, end);
        Map<LocalDate, Double> revenueByDate = new LinkedHashMap<>();
        
        // Lấy tất cả lịch hẹn đã hoàn thành hoặc đã thanh toán trong khoảng thời gian
        List<Appointment> completedAppointments = appointmentRepository
                .findByStatusInAndAppointmentDateBetweenOrderByAppointmentDateAsc(
                        Arrays.asList(AppointmentStatus.COMPLETED, AppointmentStatus.CHECKED_OUT),
                        start.atStartOfDay(), end.atTime(LocalTime.MAX));
        
        // Nhóm lịch hẹn theo ngày và tính tổng doanh thu
        Map<LocalDate, Double> revenueMap = completedAppointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAppointmentDate().toLocalDate(),
                        Collectors.summingDouble(a -> a.getTotalAmount().doubleValue())
                ));
        
        // Điền doanh thu cho từng ngày
        for (LocalDate date : dates) {
            revenueByDate.put(date, revenueMap.getOrDefault(date, 0.0));
        }
        
        return revenueByDate;
    }
    
    @Override
    public Map<String, Double> getRevenueByTimeOfDay(LocalDate start, LocalDate end) {
        // Lấy tất cả lịch hẹn đã hoàn thành hoặc đã thanh toán trong khoảng thời gian
        List<Appointment> completedAppointments = appointmentRepository
                .findByStatusInAndAppointmentDateBetweenOrderByAppointmentDateAsc(
                        Arrays.asList(AppointmentStatus.COMPLETED, AppointmentStatus.CHECKED_OUT),
                        start.atStartOfDay(), end.atTime(LocalTime.MAX));
        
        // Phân loại theo thời gian trong ngày
        Map<String, Double> revenueByTimeOfDay = new LinkedHashMap<>();
        revenueByTimeOfDay.put("Sáng (6:00-12:00)", 0.0);
        revenueByTimeOfDay.put("Chiều (12:00-18:00)", 0.0);
        revenueByTimeOfDay.put("Tối (18:00-23:00)", 0.0);
        
        for (Appointment appointment : completedAppointments) {
            LocalTime time = appointment.getAppointmentDate().toLocalTime();
            double amount = appointment.getTotalAmount().doubleValue();
            
            if (time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(12, 0))) {
                revenueByTimeOfDay.put("Sáng (6:00-12:00)", 
                        revenueByTimeOfDay.get("Sáng (6:00-12:00)") + amount);
            } else if (time.isAfter(LocalTime.of(12, 0)) && time.isBefore(LocalTime.of(18, 0))) {
                revenueByTimeOfDay.put("Chiều (12:00-18:00)", 
                        revenueByTimeOfDay.get("Chiều (12:00-18:00)") + amount);
            } else if (time.isAfter(LocalTime.of(18, 0)) && time.isBefore(LocalTime.of(23, 0))) {
                revenueByTimeOfDay.put("Tối (18:00-23:00)", 
                        revenueByTimeOfDay.get("Tối (18:00-23:00)") + amount);
            }
        }
        
        return revenueByTimeOfDay;
    }
    
    @Override
    public Map<String, Double> getRevenueByService(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        // Lấy tất cả dịch vụ đã hoàn thành trong khoảng thời gian
        List<AppointmentService> completedServices = appointmentServiceRepository.findByAppointmentDateBetween(
                startDateTime, endDateTime)
                .stream()
                .filter(as -> as.getStatus() == ServiceStatus.COMPLETED &&
                         (as.getAppointment().getStatus() == AppointmentStatus.COMPLETED || 
                          as.getAppointment().getStatus() == AppointmentStatus.CHECKED_OUT))
                .collect(Collectors.toList());
        
        // Nhóm dịch vụ theo tên và tính tổng doanh thu
        Map<String, Double> revenueByService = completedServices.stream()
                .collect(Collectors.groupingBy(
                        as -> as.getService().getName(),
                        Collectors.summingDouble(as -> as.getPrice().doubleValue())
                ));
        
        // Sắp xếp theo doanh thu giảm dần
        return revenueByService.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
} 