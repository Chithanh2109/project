package com.skincare.controller;

import com.skincare.dto.CustomerDTO;
import com.skincare.model.User;
import com.skincare.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final CustomerService customerService;

    @GetMapping
    public String showCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String appointmentCount,
            @RequestParam(required = false) Integer rating,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        
        Page<CustomerDTO> customers = customerService.getCustomers(search, status, appointmentCount, rating, pageable);
        model.addAttribute("customers", customers);
        return "admin/customers";
    }

    @PostMapping("/create")
    public String createCustomer(
            @Valid @ModelAttribute CustomerDTO customerDTO,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile avatar,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng kiểm tra lại thông tin");
            return "redirect:/admin/customers";
        }

        try {
            customerService.createCustomer(customerDTO, avatar);
            redirectAttributes.addFlashAttribute("success", "Thêm khách hàng thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/admin/customers";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getCustomerDetails(@PathVariable Long id) {
        try {
            CustomerDTO customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/toggle-status")
    @ResponseBody
    public ResponseEntity<?> toggleCustomerStatus(@PathVariable Long id) {
        try {
            customerService.toggleCustomerStatus(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/history")
    @ResponseBody
    public ResponseEntity<?> getCustomerHistory(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            return ResponseEntity.ok(customerService.getCustomerHistory(id, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/appointments")
    @ResponseBody
    public ResponseEntity<?> getCustomerAppointments(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            return ResponseEntity.ok(customerService.getCustomerAppointments(id, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/reviews")
    @ResponseBody
    public ResponseEntity<?> getCustomerReviews(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            return ResponseEntity.ok(customerService.getCustomerReviews(id, pageable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @ResponseBody
    public ResponseEntity<?> getCustomerStatistics() {
        try {
            return ResponseEntity.ok(customerService.getCustomerStatistics());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 