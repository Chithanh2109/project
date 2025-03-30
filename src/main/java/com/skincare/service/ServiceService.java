package com.skincare.service;

import com.skincare.model.Category;
import com.skincare.model.Service;
import com.skincare.repository.CategoryRepository;
import com.skincare.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public ServiceService(ServiceRepository serviceRepository, CategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }
    
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    public List<Service> getActiveServices() {
        return serviceRepository.findByActiveTrue();
    }
    
    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }
    
    public List<Service> getServicesByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại: " + categoryId));
        return serviceRepository.findByCategoryAndActiveTrue(category);
    }
    
    public List<Service> searchServices(String keyword) {
        return serviceRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword);
    }
    
    @Transactional
    public Service saveService(Service service) {
        return serviceRepository.save(service);
    }
    
    @Transactional
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }
} 