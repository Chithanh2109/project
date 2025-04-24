package com.simplebeauty.controller;

import com.simplebeauty.model.Service;
import com.simplebeauty.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/services")
public class ServiceController {

    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping
    public String getAllServices(Model model) {
        List<Service> services = serviceService.getAllServices();
        model.addAttribute("services", services);
        return "services";
    }

    @GetMapping("/{slug}")
    public String getServiceDetail(@PathVariable String slug, Model model) {
        Service service = serviceService.getServiceBySlug(slug);
        
        // Lấy các dịch vụ khác để hiển thị ở phần "Dịch vụ liên quan"
        List<Service> allServices = serviceService.getAllServices();
        allServices.removeIf(s -> s.getId().equals(service.getId()));
        List<Service> relatedServices = allServices.size() > 3 ? allServices.subList(0, 3) : allServices;
        
        model.addAttribute("service", service);
        model.addAttribute("relatedServices", relatedServices);
        
        return "service-detail";
    }
}
