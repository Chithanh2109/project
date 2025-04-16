package com.skincare.controller;

import com.skincare.model.Category;
import com.skincare.model.Service;
import com.skincare.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    
    private final ServiceService serviceService;
    
    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }
    
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        List<Service> services = serviceService.getAllServices();
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Service>> getActiveServices() {
        List<Service> services = serviceService.getActiveServices();
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        Optional<Service> service = serviceService.getServiceById(id);
        return service.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Service>> getServicesByCategory(@PathVariable Long categoryId) {
        try {
            List<Service> services = serviceService.getServicesByCategory(categoryId);
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Service>> searchServices(@RequestParam String keyword) {
        List<Service> services = serviceService.searchServices(keyword);
        return ResponseEntity.ok(services);
    }
    
    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service savedService = serviceService.saveService(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedService);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service service) {
        Optional<Service> existingService = serviceService.getServiceById(id);
        
        if (existingService.isPresent()) {
            service.setId(id);
            Service updatedService = serviceService.saveService(service);
            return ResponseEntity.ok(updatedService);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        try {
            serviceService.deleteService(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = serviceService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/categories/active")
    public ResponseEntity<List<Category>> getActiveCategories() {
        List<Category> categories = serviceService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }
} 