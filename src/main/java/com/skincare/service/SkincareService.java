package com.skincare.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.skincare.model.Category;
import com.skincare.model.Quiz;
import com.skincare.model.Service;
import com.skincare.model.ServiceStatus;
import com.skincare.repository.CategoryRepository;
import com.skincare.repository.QuizRepository;
import com.skincare.repository.ServiceRepository;

@org.springframework.stereotype.Service
public class SkincareService implements ServiceService {
    
    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    
    @Autowired
    public SkincareService(ServiceRepository serviceRepository, CategoryRepository categoryRepository, QuizRepository quizRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
    }
    
    private List<Service> services = new ArrayList<>();
    
    /**
     * Initialize sample services and categories for demo purposes
     */
    public void initSampleData() {
        // Create some sample categories if they don't exist
        if (categoryRepository.count() == 0) {
            Category facialCategory = new Category();
            facialCategory.setName("Chăm sóc da mặt");
            facialCategory.setDescription("Các dịch vụ chăm sóc, điều trị da mặt chuyên sâu");
            categoryRepository.save(facialCategory);
            
            Category bodyCategory = new Category();
            bodyCategory.setName("Chăm sóc body");
            bodyCategory.setDescription("Các dịch vụ chăm sóc da toàn thân");
            categoryRepository.save(bodyCategory);
            
            Category spaCategory = new Category();
            spaCategory.setName("Massage & Spa");
            spaCategory.setDescription("Các dịch vụ massage thư giãn, trị liệu");
            categoryRepository.save(spaCategory);
        }
        
        // Create some sample services if they don't exist
        if (serviceRepository.count() == 0) {
            List<Category> categories = categoryRepository.findAll();
            Category facialCategory = categories.stream()
                    .filter(cat -> cat.getName().contains("da mặt"))
                    .findFirst()
                    .orElse(categories.get(0));
            
            Category bodyCategory = categories.stream()
                    .filter(cat -> cat.getName().contains("body"))
                    .findFirst()
                    .orElse(categories.get(0));
            
            // Basic Facial
            Service service1 = new Service();
            service1.setName("Basic Facial");
            service1.setDescription("Dịch vụ chăm sóc da cơ bản, phù hợp với mọi loại da");
            service1.setPrice(new java.math.BigDecimal("500000"));
            service1.setDuration(60);
            service1.setCategory(facialCategory);
            service1.setStatus(ServiceStatus.PENDING);
            service1.setFeatured(true);
            serviceRepository.save(service1);
            
            // Acne Treatment
            Service service2 = new Service();
            service2.setName("Acne Treatment");
            service2.setDescription("Điều trị mụn chuyên sâu, giảm viêm và ngăn ngừa sẹo");
            service2.setPrice(new java.math.BigDecimal("700000"));
            service2.setDuration(75);
            service2.setCategory(facialCategory);
            service2.setStatus(ServiceStatus.PENDING);
            service2.setFeatured(false);
            serviceRepository.save(service2);
            
            // Body Scrub
            Service service3 = new Service();
            service3.setName("Body Scrub");
            service3.setDescription("Tẩy tế bào chết toàn thân, làm mềm và sáng da");
            service3.setPrice(new java.math.BigDecimal("600000"));
            service3.setDuration(90);
            service3.setCategory(bodyCategory);
            service3.setStatus(ServiceStatus.PENDING);
            service3.setFeatured(true);
            serviceRepository.save(service3);
        }
    }
    
    @Override
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    @Override
    public List<Service> getActiveServices() {
        return serviceRepository.findByActiveTrue();
    }
    
    @Override
    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }
    
    @Override
    public List<Service> getServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategory_Id(categoryId);
    }
    
    @Override
    public List<Service> searchServices(String keyword) {
        return serviceRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    @Override
    public Service saveService(Service service) {
        return serviceRepository.save(service);
    }
    
    @Override
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }
    
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public List<Category> getActiveCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    @Override
    public Quiz getActiveQuiz() {
        return quizRepository.findByActiveTrue().orElse(null);
    }
    
    @Override
    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }
    
    @Override
    public List<Service> getRelatedServices(Long serviceId) {
        // Get the current service
        Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
        if (!serviceOpt.isPresent()) {
            return Collections.emptyList();
        }
        
        Service service = serviceOpt.get();
        
        // Get services from the same category
        return serviceRepository.findByCategoryAndIdNot(service.getCategory(), serviceId)
                .stream()
                .limit(4)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Service> getPopularServices() {
        // Return featured services as popular services
        // In a real implementation, this would typically be based on booking statistics
        return serviceRepository.findByFeaturedTrue();
    }
    
    @Override
    public List<Service> getFeaturedServices() {
        return serviceRepository.findByFeaturedTrue();
    }
    
    @Override
    public List<Service> getAllActiveServices() {
        return serviceRepository.findByStatus(ServiceStatus.PENDING);
    }
    
    @Override
    public List<Service> getActiveServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategoryIdAndActiveTrue(categoryId);
    }
} 