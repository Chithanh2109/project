package com.skincare.controller;

import com.skincare.model.Service;
import com.skincare.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public String showServices(Model model) {
        List<Service> services = serviceService.getAllServices();
        model.addAttribute("services", services);
        return "admin/services";
    }

    @PostMapping
    public String createService(
            @Valid @ModelAttribute Service service,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/services";
        }

        try {
            serviceService.createService(service);
            redirectAttributes.addFlashAttribute("success", "Thêm dịch vụ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/services";
    }

    @PutMapping("/{id}")
    public String updateService(
            @PathVariable Long id,
            @Valid @ModelAttribute Service service,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/services";
        }

        try {
            serviceService.updateService(id, service);
            redirectAttributes.addFlashAttribute("success", "Cập nhật dịch vụ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/services";
    }

    @DeleteMapping("/{id}")
    public String deleteService(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        
        try {
            serviceService.deleteService(id);
            redirectAttributes.addFlashAttribute("success", "Xóa dịch vụ thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleServiceStatus(@PathVariable Long id) {
        try {
            Service service = serviceService.toggleServiceStatus(id);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 