package com.skincare.controller;

import com.skincare.dto.AppointmentDTO;
import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.model.Service;
import com.skincare.model.User;
import com.skincare.service.AppointmentService;
import com.skincare.service.ServiceService;
import com.skincare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ServiceService serviceService;
    private final UserService userService;

    @GetMapping("/book")
    public String showBookingForm(Model model) {
        List<Service> services = serviceService.getAllServices();
        List<User> specialists = userService.getAllSpecialists();
        
        model.addAttribute("appointmentDTO", new AppointmentDTO());
        model.addAttribute("services", services);
        model.addAttribute("specialists", specialists);
        
        return "appointment-booking";
    }

    @PostMapping("/book")
    public String bookAppointment(
            @Valid @ModelAttribute AppointmentDTO appointmentDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            return "appointment-booking";
        }

        try {
            Appointment appointment = appointmentService.createAppointment(appointmentDTO, currentUser.getId());
            redirectAttributes.addFlashAttribute("success", 
                "Đặt lịch thành công! Vui lòng kiểm tra email để xem chi tiết.");
            return "redirect:/appointments/my-appointments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/appointments/book";
        }
    }

    @GetMapping("/my-appointments")
    public String showMyAppointments(@AuthenticationPrincipal User currentUser, Model model) {
        List<Appointment> appointments = appointmentService.getCustomerAppointments(currentUser.getId());
        model.addAttribute("appointments", appointments);
        return "my-appointments";
    }

    @GetMapping("/specialist/schedule")
    public String showSpecialistSchedule(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) LocalDate date,
            Model model) {
        
        if (date == null) {
            date = LocalDate.now();
        }

        List<Appointment> appointments = appointmentService.getSpecialistAppointments(currentUser.getId());
        model.addAttribute("appointments", appointments);
        model.addAttribute("currentDate", date);
        
        return "specialist-schedule";
    }

    @PostMapping("/{id}/status")
    @ResponseBody
    public String updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        
        appointmentService.updateAppointmentStatus(id, status);
        return "success";
    }

    @GetMapping("/available-times")
    @ResponseBody
    public List<String> getAvailableTimes(
            @RequestParam Long specialistId,
            @RequestParam String date) {
        // TODO: Implement logic to get available time slots
        return List.of("09:00", "09:30", "10:00", "10:30", "11:00");
    }
} 