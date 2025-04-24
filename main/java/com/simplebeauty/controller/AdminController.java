package com.simplebeauty.controller;

import com.simplebeauty.model.Appointment;
import com.simplebeauty.model.AppointmentStatus;
import com.simplebeauty.model.Service;
import com.simplebeauty.model.User;
import com.simplebeauty.service.AppointmentService;
import com.simplebeauty.service.ServiceService;
import com.simplebeauty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ServiceService serviceService;
    private final AppointmentService appointmentService;

    @Autowired
    public AdminController(UserService userService, ServiceService serviceService, AppointmentService appointmentService) {
        this.userService = userService;
        this.serviceService = serviceService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<Appointment> recentAppointments = appointmentService.getAllAppointments();
        List<User> users = userService.findAllUsers();
        
        model.addAttribute("appointmentCount", recentAppointments.size());
        model.addAttribute("userCount", users.size());
        model.addAttribute("serviceCount", serviceService.getAllServices().size());
        model.addAttribute("recentAppointments", recentAppointments.size() > 5 ? recentAppointments.subList(0, 5) : recentAppointments);
        
        return "admin/dashboard";
    }

    // Quản lý dịch vụ
    @GetMapping("/services")
    public String services(Model model) {
        List<Service> services = serviceService.getAllServices();
        model.addAttribute("services", services);
        return "admin/services/list";
    }

    @GetMapping("/services/new")
    public String newServiceForm(Model model) {
        model.addAttribute("service", new Service());
        model.addAttribute("pageTitle", "Thêm dịch vụ mới");
        return "admin/services/form";
    }

    @PostMapping("/services/new")
    public String createService(@ModelAttribute Service service, Model model) {
        try {
            serviceService.createService(service);
            return "redirect:/admin/services?success=created";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", "Thêm dịch vụ mới");
            return "admin/services/form";
        }
    }

    @GetMapping("/services/edit/{id}")
    public String editServiceForm(@PathVariable Long id, Model model) {
        Service service = serviceService.getServiceById(id);
        model.addAttribute("service", service);
        model.addAttribute("pageTitle", "Chỉnh sửa dịch vụ");
        return "admin/services/form";
    }

    @PostMapping("/services/edit/{id}")
    public String updateService(@PathVariable Long id, @ModelAttribute Service service, Model model) {
        try {
            serviceService.updateService(id, service);
            return "redirect:/admin/services?success=updated";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("pageTitle", "Chỉnh sửa dịch vụ");
            return "admin/services/form";
        }
    }

    @GetMapping("/services/delete/{id}")
    public String deleteService(@PathVariable Long id) {
        serviceService.deleteService(id);
        return "redirect:/admin/services?success=deleted";
    }

    // Quản lý lịch hẹn
    @GetMapping("/appointments")
    public String appointments(Model model) {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        model.addAttribute("appointments", appointments);
        return "admin/appointments/list";
    }

    @GetMapping("/appointments/{id}")
    public String viewAppointment(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        model.addAttribute("appointment", appointment);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "admin/appointments/detail";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(@PathVariable Long id, @RequestParam AppointmentStatus status) {
        appointmentService.updateAppointmentStatus(id, status);
        return "redirect:/admin/appointments/" + id + "?success=updated";
    }

    // Quản lý người dùng
    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users/list";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        List<Appointment> userAppointments = appointmentService.getAppointmentsByUser(user);
        
        model.addAttribute("user", user);
        model.addAttribute("appointments", userAppointments);
        
        return "admin/users/detail";
    }
}
