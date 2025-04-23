package com.skincare.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skincare.model.Category;
import com.skincare.model.Quiz;
import com.skincare.model.ServiceStatus;
import com.skincare.repository.CategoryRepository;
import com.skincare.repository.QuizRepository;
import com.skincare.repository.ServiceRepository;

@Service
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
    
    private List<com.skincare.model.Service> services = new ArrayList<>();
    
    // Phương thức tạo dữ liệu mẫu khi khởi động ứng dụng
    public void initSampleData() {
        if (!services.isEmpty()) {
            return;
        }
        
        // Tạo các danh mục
        Category facialCategory = new Category();
        facialCategory.setId(1L);
        facialCategory.setName("Facial Treatments");
        facialCategory.setDescription("Various facial treatments for different skin types");
        
        Category bodyCategory = new Category();
        bodyCategory.setId(2L);
        bodyCategory.setName("Body Treatments");
        bodyCategory.setDescription("Body care and massage treatments");
        
        Category acneCategory = new Category();
        acneCategory.setId(3L);
        acneCategory.setName("Acne & Scar Treatments");
        acneCategory.setDescription("Specialized treatments for acne and scars");
        
        // Tạo dịch vụ mẫu
        com.skincare.model.Service basicFacial = new com.skincare.model.Service();
        basicFacial.setId(1L);
        basicFacial.setName("Basic Facial");
        basicFacial.setDescription("A deep cleansing facial that includes exfoliation, extraction, and hydration");
        basicFacial.setPrice(new BigDecimal("500000"));
        basicFacial.setDuration(60);
        basicFacial.setCategory(facialCategory);
        basicFacial.setStatus(ServiceStatus.PENDING);
        basicFacial.setFeatured(true);
        
        com.skincare.model.Service advancedFacial = new com.skincare.model.Service();
        advancedFacial.setId(2L);
        advancedFacial.setName("Advanced Facial");
        advancedFacial.setDescription("Premium facial with anti-aging serums and specialized massage techniques");
        advancedFacial.setPrice(new BigDecimal("800000"));
        advancedFacial.setDuration(90);
        advancedFacial.setCategory(facialCategory);
        advancedFacial.setStatus(ServiceStatus.PENDING);
        advancedFacial.setFeatured(true);
        
        com.skincare.model.Service bodyScrub = new com.skincare.model.Service();
        bodyScrub.setId(3L);
        bodyScrub.setName("Body Scrub & Massage");
        bodyScrub.setDescription("Full body exfoliation followed by a relaxing massage");
        bodyScrub.setPrice(new BigDecimal("1200000"));
        bodyScrub.setDuration(120);
        bodyScrub.setCategory(bodyCategory);
        bodyScrub.setStatus(ServiceStatus.PENDING);
        bodyScrub.setFeatured(false);
        
        com.skincare.model.Service acneTreatment = new com.skincare.model.Service();
        acneTreatment.setId(4L);
        acneTreatment.setName("Acne Treatment");
        acneTreatment.setDescription("Specialized treatment to reduce acne and prevent breakouts");
        acneTreatment.setPrice(new BigDecimal("650000"));
        acneTreatment.setDuration(75);
        acneTreatment.setCategory(acneCategory);
        acneTreatment.setStatus(ServiceStatus.PENDING);
        acneTreatment.setFeatured(false);
        
        // Thêm vào danh sách
        services.add(basicFacial);
        services.add(advancedFacial);
        services.add(bodyScrub);
        services.add(acneTreatment);
    }
    
    @Override
    public List<com.skincare.model.Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    @Override
    public List<com.skincare.model.Service> getActiveServices() {
        return serviceRepository.findByStatus(ServiceStatus.PENDING);
    }
    
    @Override
    public Optional<com.skincare.model.Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }
    
    @Override
    public List<com.skincare.model.Service> getServicesByCategory(Long categoryId) {
        return serviceRepository.findByCategory_Id(categoryId);
    }
    
    @Override
    public List<com.skincare.model.Service> searchServices(String keyword) {
        return serviceRepository.findByNameContainingIgnoreCase(keyword);
    }
    
    @Override
    public com.skincare.model.Service saveService(com.skincare.model.Service service) {
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
        return categoryRepository.findByStatusOrderByNameAsc("ACTIVE");
    }
    
    @Override
    public List<com.skincare.model.Service> getFeaturedServices() {
        return serviceRepository.findByFeaturedTrue();
    }
    
    @Override
    public List<com.skincare.model.Service> getAllActiveServices() {
        return serviceRepository.findByStatus(ServiceStatus.PENDING);
    }
    
    @Override
    public List<com.skincare.model.Service> getRelatedServices(Long serviceId) {
        com.skincare.model.Service service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        return serviceRepository.findByCategoryAndIdNot(service.getCategory(), serviceId);
    }
    
    @Override
    public Quiz getActiveQuiz() {
        return quizRepository.findByActiveTrue()
            .orElseThrow(() -> new RuntimeException("No active quiz found"));
    }
} 