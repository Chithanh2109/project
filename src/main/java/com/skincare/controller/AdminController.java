package com.skincare.controller;

import com.skincare.model.Appointment;
import com.skincare.model.Service;
import com.skincare.model.User;
import com.skincare.model.UserType;
import com.skincare.model.AppointmentStatus;
import com.skincare.service.AppointmentManagementService;
import com.skincare.service.ServiceService;
import com.skincare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AppointmentManagementService appointmentService;
    private final ServiceService serviceService;
    private final UserService userService;

    @Autowired
    public AdminController(AppointmentManagementService appointmentService,
                          ServiceService serviceService,
                          UserService userService) {
        this.appointmentService = appointmentService;
        this.serviceService = serviceService;
        this.userService = userService;
    }

    @GetMapping
    public String dashboard(Model model) {
        // Lấy các lịch hẹn sắp tới
        List<Appointment> upcomingAppointments = appointmentService.getUpcomingAppointments();
        
        // Thống kê dữ liệu cho dashboard
        long pendingCount = appointmentService.getAppointmentsByStatus(AppointmentStatus.PENDING).size();
        long todayCount = appointmentService.getAppointmentsByDate(LocalDate.now()).size();
        long totalCustomers = userService.getUsersByType(UserType.CUSTOMER).size();
        
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("todayCount", todayCount);
        model.addAttribute("customerCount", totalCustomers);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        
        return "admin/dashboard";
    }

    @GetMapping("/appointments")
    public String appointments(Model model) {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        return "admin/appointments";
    }

    @GetMapping("/appointments/{id}")
    public String appointmentDetail(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại"));
        
        List<User> therapists = userService.getUsersByType(UserType.THERAPIST);
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("therapists", therapists);
        
        return "admin/appointment-detail";
    }

    @PostMapping("/appointments/{id}/check-in")
    public String checkInAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.checkInAppointment(id);
            redirectAttributes.addFlashAttribute("success", "Check-in thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/appointments/" + id;
    }

    @PostMapping("/appointments/{id}/check-out")
    public String checkOutAppointment(@PathVariable Long id, @RequestParam String paymentMethod, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.checkOutAppointment(id, paymentMethod);
            redirectAttributes.addFlashAttribute("success", "Check-out thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/appointments/" + id;
    }

    @PostMapping("/appointments/{id}/assign/{therapistId}")
    public String assignTherapist(@PathVariable Long id, @PathVariable Long therapistId, 
                                 RedirectAttributes redirectAttributes) {
        try {
            appointmentService.assignTherapist(id, therapistId);
            redirectAttributes.addFlashAttribute("success", "Phân công chuyên viên thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/appointments/" + id;
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointment(id, reason, false);
            redirectAttributes.addFlashAttribute("success", "Hủy lịch hẹn thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/appointments/" + id;
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", serviceService.getAllServices());
        model.addAttribute("categories", serviceService.getAllCategories());
        return "admin/services";
    }

    @GetMapping("/services/new")
    public String newServiceForm(Model model) {
        model.addAttribute("service", new Service());
        model.addAttribute("categories", serviceService.getAllCategories());
        return "admin/service-form";
    }

    @PostMapping("/services/save")
    public String saveService(@ModelAttribute Service service, RedirectAttributes redirectAttributes) {
        try {
            serviceService.saveService(service);
            redirectAttributes.addFlashAttribute("success", "Dịch vụ đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/services/edit/{id}")
    public String editServiceForm(@PathVariable Long id, Model model) {
        Service service = serviceService.getServiceById(id)
            .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại"));
        
        model.addAttribute("service", service);
        model.addAttribute("categories", serviceService.getAllCategories());
        return "admin/service-form";
    }

    @GetMapping("/therapists")
    public String therapists(Model model) {
        model.addAttribute("therapists", userService.getUsersByType(UserType.THERAPIST));
        return "admin/therapists";
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        List<User> customers = userService.getUsersByType(UserType.CUSTOMER);
        model.addAttribute("customers", customers);
        return "admin/customers";
    }

    @GetMapping("/customers/{id}")
    public String customerDetail(@PathVariable Long id, Model model) {
        User customer = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        
        List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(id);
        
        model.addAttribute("customer", customer);
        model.addAttribute("appointments", appointments);
        
        return "admin/customer-detail";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        // Thêm dữ liệu báo cáo
        return "admin/reports";
    }
} 