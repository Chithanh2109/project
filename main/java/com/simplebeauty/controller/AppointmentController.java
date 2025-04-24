package com.simplebeauty.controller;

import com.simplebeauty.model.Appointment;
import com.simplebeauty.model.AppointmentStatus;
import com.simplebeauty.model.Service;
import com.simplebeauty.model.User;
import com.simplebeauty.service.AppointmentService;
import com.simplebeauty.service.ServiceService;
import com.simplebeauty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ServiceService serviceService;
    private final UserService userService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, ServiceService serviceService, UserService userService) {
        this.appointmentService = appointmentService;
        this.serviceService = serviceService;
        this.userService = userService;
    }

    @GetMapping
    public String myAppointments(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        
        List<Appointment> appointments = appointmentService.getAppointmentsByUser(user);
        
        model.addAttribute("appointments", appointments);
        model.addAttribute("pageTitle", "Lịch hẹn của tôi - SimpleBeauty");
        
        return "appointments/my-appointments";
    }

    @GetMapping("/book")
    public String bookAppointmentForm(@RequestParam(required = false) Long serviceId, Model model) {
        List<Service> services = serviceService.getAllServices();
        
        model.addAttribute("services", services);
        model.addAttribute("selectedServiceId", serviceId);
        model.addAttribute("pageTitle", "Đặt lịch hẹn - SimpleBeauty");
        
        return "appointments/book";
    }

    @PostMapping("/book")
    public String bookAppointment(
            @RequestParam Long serviceId,
            @RequestParam String appointmentDate,
            @RequestParam String appointmentTime,
            @RequestParam(required = false) String notes,
            Model model) {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByEmail(auth.getName());
            
            LocalDate date = LocalDate.parse(appointmentDate);
            LocalTime time = LocalTime.parse(appointmentTime);
            LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);
            
            if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                model.addAttribute("error", "Thời gian đặt lịch phải là tương lai");
                return bookAppointmentForm(serviceId, model);
            }
            
            appointmentService.createAppointment(user.getId(), serviceId, appointmentDateTime, notes);
            
            return "redirect:/appointments?success=booked";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return bookAppointmentForm(serviceId, model);
        }
    }

    @GetMapping("/{id}")
    public String viewAppointment(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        
        Appointment appointment = appointmentService.getAppointmentById(id);
        
        // Kiểm tra xem người dùng có quyền xem lịch hẹn này không
        if (!appointment.getUser().getId().equals(user.getId())) {
            return "redirect:/appointments?error=unauthorized";
        }
        
        model.addAttribute("appointment", appointment);
        model.addAttribute("pageTitle", "Chi tiết lịch hẹn - SimpleBeauty");
        
        return "appointments/detail";
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        
        Appointment appointment = appointmentService.getAppointmentById(id);
        
        // Kiểm tra xem người dùng có quyền hủy lịch hẹn này không
        if (!appointment.getUser().getId().equals(user.getId())) {
            return "redirect:/appointments?error=unauthorized";
        }
        
        // Chỉ cho phép hủy lịch hẹn nếu trạng thái là PENDING hoặc CONFIRMED
        if (appointment.getStatus() == AppointmentStatus.PENDING || appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            appointmentService.cancelAppointment(id);
            return "redirect:/appointments?success=cancelled";
        } else {
            return "redirect:/appointments/" + id + "?error=cannot-cancel";
        }
    }
}
