package com.skincare.controller;

import com.skincare.model.SkinCareService;
import com.skincare.model.SkinCareService.ServiceCategory;
import com.skincare.model.SkinConcern;
import com.skincare.model.SkinType;
import com.skincare.service.SkinCareServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller xử lý các request liên quan đến dịch vụ chăm sóc da
 */
@Controller
@RequestMapping("/services")
public class SkinCareServiceController {

    private final SkinCareServiceService serviceService;

    @Autowired
    public SkinCareServiceController(SkinCareServiceService serviceService) {
        this.serviceService = serviceService;
    }

    /**
     * Hiển thị trang danh sách dịch vụ
     */
    @GetMapping
    public String getAllServices(Model model) {
        List<SkinCareService> services = serviceService.getAllActiveServices();
        model.addAttribute("services", services);
        model.addAttribute("categories", ServiceCategory.values());
        model.addAttribute("skinTypes", SkinType.values());
        model.addAttribute("skinConcerns", SkinConcern.values());
        return "services/list";
    }

    /**
     * Hiển thị chi tiết một dịch vụ
     */
    @GetMapping("/{id}")
    public String getServiceDetail(@PathVariable("id") Long id, Model model) {
        Optional<SkinCareService> serviceOpt = serviceService.getServiceById(id);
        if (serviceOpt.isPresent()) {
            model.addAttribute("service", serviceOpt.get());
            return "services/detail";
        }
        return "redirect:/services?error=ServiceNotFound";
    }

    /**
     * Tìm kiếm dịch vụ theo tên
     */
    @GetMapping("/search")
    public String searchServices(@RequestParam("keyword") String keyword, Model model) {
        List<SkinCareService> services = serviceService.searchServicesByName(keyword);
        model.addAttribute("services", services);
        model.addAttribute("keyword", keyword);
        return "services/list";
    }

    /**
     * Lọc dịch vụ theo danh mục
     */
    @GetMapping("/category/{category}")
    public String getServicesByCategory(@PathVariable("category") String categoryName, Model model) {
        try {
            ServiceCategory category = ServiceCategory.valueOf(categoryName.toUpperCase());
            List<SkinCareService> services = serviceService.getServicesByCategory(category);
            model.addAttribute("services", services);
            model.addAttribute("selectedCategory", category);
            return "services/list";
        } catch (IllegalArgumentException e) {
            return "redirect:/services?error=InvalidCategory";
        }
    }

    /**
     * API để lấy dịch vụ theo loại da
     */
    @GetMapping("/api/skin-type/{skinType}")
    @ResponseBody
    public ResponseEntity<List<SkinCareService>> getServicesBySkinType(@PathVariable("skinType") String skinTypeName) {
        try {
            SkinType skinType = SkinType.valueOf(skinTypeName.toUpperCase());
            List<SkinCareService> services = serviceService.getServicesBySkinType(skinType);
            return ResponseEntity.ok(services);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * API để lấy dịch vụ theo vấn đề da
     */
    @GetMapping("/api/skin-concern/{skinConcern}")
    @ResponseBody
    public ResponseEntity<List<SkinCareService>> getServicesBySkinConcern(@PathVariable("skinConcern") String skinConcernName) {
        try {
            SkinConcern skinConcern = SkinConcern.valueOf(skinConcernName.toUpperCase());
            List<SkinCareService> services = serviceService.getServicesBySkinConcern(skinConcern);
            return ResponseEntity.ok(services);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * API để lấy các dịch vụ phổ biến nhất
     */
    @GetMapping("/api/popular")
    @ResponseBody
    public ResponseEntity<List<SkinCareService>> getPopularServices(@RequestParam(defaultValue = "5") int limit) {
        List<SkinCareService> services = serviceService.getMostPopularServices(limit);
        return ResponseEntity.ok(services);
    }

    /**
     * API để lấy các dịch vụ được đánh giá cao nhất
     */
    @GetMapping("/api/top-rated")
    @ResponseBody
    public ResponseEntity<List<SkinCareService>> getTopRatedServices(@RequestParam(defaultValue = "5") int limit) {
        List<SkinCareService> services = serviceService.getTopRatedServices(limit);
        return ResponseEntity.ok(services);
    }

    /**
     * ========= ADMIN APIs =========
     */

    /**
     * Hiển thị form tạo dịch vụ mới (admin)
     */
    @GetMapping("/admin/create")
    public String showCreateForm(Model model) {
        model.addAttribute("service", new SkinCareService());
        model.addAttribute("categories", ServiceCategory.values());
        model.addAttribute("skinTypes", SkinType.values());
        model.addAttribute("skinConcerns", SkinConcern.values());
        return "admin/services/create";
    }

    /**
     * Xử lý tạo dịch vụ mới (admin)
     */
    @PostMapping("/admin/create")
    public String createService(@ModelAttribute SkinCareService service) {
        serviceService.createService(service);
        return "redirect:/services/admin?success=ServiceCreated";
    }

    /**
     * Hiển thị form cập nhật dịch vụ (admin)
     */
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<SkinCareService> serviceOpt = serviceService.getServiceById(id);
        if (serviceOpt.isPresent()) {
            model.addAttribute("service", serviceOpt.get());
            model.addAttribute("categories", ServiceCategory.values());
            model.addAttribute("skinTypes", SkinType.values());
            model.addAttribute("skinConcerns", SkinConcern.values());
            return "admin/services/edit";
        }
        return "redirect:/services/admin?error=ServiceNotFound";
    }

    /**
     * Xử lý cập nhật dịch vụ (admin)
     */
    @PostMapping("/admin/edit/{id}")
    public String updateService(@PathVariable("id") Long id, @ModelAttribute SkinCareService service) {
        service.setId(id);
        serviceService.updateService(service);
        return "redirect:/services/admin?success=ServiceUpdated";
    }

    /**
     * Xử lý xóa dịch vụ (admin)
     */
    @PostMapping("/admin/delete/{id}")
    public String deleteService(@PathVariable("id") Long id) {
        serviceService.deleteService(id);
        return "redirect:/services/admin?success=ServiceDeleted";
    }

    /**
     * API để bật/tắt trạng thái dịch vụ (admin)
     */
    @PostMapping("/admin/api/toggle/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> toggleServiceActive(@PathVariable("id") Long id) {
        try {
            boolean active = serviceService.toggleServiceActive(id);
            return ResponseEntity.ok(active);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }
} 