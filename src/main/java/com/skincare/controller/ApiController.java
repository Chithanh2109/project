package com.skincare.controller;

import com.skincare.model.Service;
import com.skincare.model.User;
import com.skincare.dto.SearchResultDTO;
import com.skincare.service.ServiceService;
import com.skincare.service.TherapistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ServiceService serviceService;
    private final TherapistService therapistService;

    @Autowired
    public ApiController(ServiceService serviceService, TherapistService therapistService) {
        this.serviceService = serviceService;
        this.therapistService = therapistService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchResultDTO>> search(@RequestParam String query) {
        if (query == null || query.trim().isEmpty() || query.length() < 2) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<SearchResultDTO> results = new ArrayList<>();
        
        // Search in services
        List<Service> services = serviceService.getActiveServices().stream()
                .filter(service -> service.getName().toLowerCase().contains(query.toLowerCase()) || 
                                  (service.getDescription() != null && 
                                   service.getDescription().toLowerCase().contains(query.toLowerCase())))
                .limit(5)
                .collect(Collectors.toList());
        
        for (Service service : services) {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setId(service.getId());
            dto.setName(service.getName());
            dto.setType("Dịch vụ");
            dto.setUrl("/services/" + service.getId());
            dto.setImage(service.getImageUrl());
            results.add(dto);
        }
        
        // Search in therapists
        List<User> therapists = therapistService.getAllTherapists().stream()
                .filter(therapist -> 
                    (therapist.getFirstName() + " " + therapist.getLastName()).toLowerCase().contains(query.toLowerCase()) ||
                    (therapist.getSpecialization() != null && 
                     therapist.getSpecialization().toLowerCase().contains(query.toLowerCase())))
                .limit(5)
                .collect(Collectors.toList());
        
        for (User therapist : therapists) {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setId(therapist.getId());
            dto.setName(therapist.getFirstName() + " " + therapist.getLastName());
            dto.setType("Chuyên viên");
            dto.setUrl("/therapists/" + therapist.getId());
            dto.setImage(therapist.getAvatarUrl());
            results.add(dto);
        }
        
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/services/featured")
    public ResponseEntity<List<Service>> getFeaturedServices() {
        return ResponseEntity.ok(serviceService.getFeaturedServices());
    }
    
    @GetMapping("/services/related")
    public ResponseEntity<List<Service>> getRelatedServices(@RequestParam Long serviceId) {
        return ResponseEntity.ok(serviceService.getRelatedServices(serviceId));
    }
    
    @GetMapping("/services/popular")
    public ResponseEntity<List<Service>> getPopularServices() {
        return ResponseEntity.ok(serviceService.getPopularServices());
    }
    
    @GetMapping("/therapists/all")
    public ResponseEntity<List<User>> getAllTherapists() {
        return ResponseEntity.ok(therapistService.getAllTherapists());
    }
} 