package com.skincare.controller;

import com.skincare.dto.DashboardStats;
import com.skincare.dto.ServiceStat;
import com.skincare.dto.TherapistStat;
import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.service.AppointmentManagementService;
import com.skincare.service.ReportService;
import com.skincare.service.ServiceService;
import com.skincare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {
    
    private final AppointmentManagementService appointmentService;
    private final UserService userService;
    private final ServiceService serviceService;
    private final ReportService reportService;
    
    @Autowired
    public AdminDashboardController(
            AppointmentManagementService appointmentService,
            UserService userService,
            ServiceService serviceService,
            ReportService reportService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.serviceService = serviceService;
        this.reportService = reportService;
    }
    
    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(
            @RequestParam(name = "start", required = false) String startDate,
            @RequestParam(name = "end", required = false) String endDate,
            Model model) {
        
        // Mặc định lấy dữ liệu của 30 ngày gần nhất
        LocalDate start = startDate != null ? 
                LocalDate.parse(startDate) : 
                LocalDate.now().minusDays(30);
        
        LocalDate end = endDate != null ? 
                LocalDate.parse(endDate) : 
                LocalDate.now();
        
        // Lấy thống kê tổng hợp
        DashboardStats stats = reportService.getDashboardStats(start, end);
        model.addAttribute("stats", stats);
        
        // Dữ liệu biểu đồ lịch hẹn theo ngày
        List<LocalDate> dates = reportService.getDateRange(start, end);
        List<Long> appointmentCounts = reportService.getAppointmentCountsByDate(start, end);
        
        model.addAttribute("appointmentDates", dates.stream()
                .map(date -> date.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList()));
        model.addAttribute("appointmentCounts", appointmentCounts);
        
        // Dữ liệu biểu đồ trạng thái lịch hẹn
        List<String> statusLabels = new ArrayList<>();
        List<Long> statusCounts = new ArrayList<>();
        
        for (AppointmentStatus status : AppointmentStatus.values()) {
            statusLabels.add(getStatusDisplayName(status));
            statusCounts.add(reportService.getAppointmentCountByStatus(status, start, end));
        }
        
        model.addAttribute("statusLabels", statusLabels);
        model.addAttribute("statusCounts", statusCounts);
        
        // Danh sách dịch vụ phổ biến
        List<ServiceStat> popularServices = reportService.getPopularServices(start, end, 5);
        model.addAttribute("popularServices", popularServices);
        
        // Danh sách chuyên viên xuất sắc
        List<TherapistStat> topTherapists = reportService.getTopTherapists(start, end, 5);
        model.addAttribute("topTherapists", topTherapists);
        
        // Lịch hẹn gần đây
        List<Appointment> recentAppointments = appointmentService.getRecentAppointments(10);
        model.addAttribute("recentAppointments", recentAppointments);
        
        return "admin/dashboard";
    }
    
    private String getStatusDisplayName(AppointmentStatus status) {
        switch (status) {
            case PENDING: return "Chờ xác nhận";
            case CONFIRMED: return "Đã xác nhận";
            case CHECKED_IN: return "Đã đến";
            case IN_PROGRESS: return "Đang thực hiện";
            case COMPLETED: return "Hoàn thành";
            case CHECKED_OUT: return "Đã thanh toán";
            case CANCELLED: return "Đã hủy";
            case NO_SHOW: return "Vắng mặt";
            default: return status.name();
        }
    }
} 