package com.skincare.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skincare.model.Category;
import com.skincare.repository.CategoryRepository;
import com.skincare.repository.ServiceRepository;

@Service
public class ServiceService {
    
    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public ServiceService(ServiceRepository serviceRepository, CategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
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
        basicFacial.setStatus("ACTIVE");
        basicFacial.setFeatured(true);
        
        com.skincare.model.Service advancedFacial = new com.skincare.model.Service();
        advancedFacial.setId(2L);
        advancedFacial.setName("Advanced Facial");
        advancedFacial.setDescription("Premium facial with anti-aging serums and specialized massage techniques");
        advancedFacial.setPrice(new BigDecimal("800000"));
        advancedFacial.setDuration(90);
        advancedFacial.setCategory(facialCategory);
        advancedFacial.setStatus("ACTIVE");
        advancedFacial.setFeatured(true);
        
        com.skincare.model.Service bodyScrub = new com.skincare.model.Service();
        bodyScrub.setId(3L);
        bodyScrub.setName("Body Scrub & Massage");
        bodyScrub.setDescription("Full body exfoliation followed by a relaxing massage");
        bodyScrub.setPrice(new BigDecimal("1200000"));
        bodyScrub.setDuration(120);
        bodyScrub.setCategory(bodyCategory);
        bodyScrub.setStatus("ACTIVE");
        bodyScrub.setFeatured(false);
        
        com.skincare.model.Service acneTreatment = new com.skincare.model.Service();
        acneTreatment.setId(4L);
        acneTreatment.setName("Acne Treatment");
        acneTreatment.setDescription("Specialized treatment to reduce acne and prevent breakouts");
        acneTreatment.setPrice(new BigDecimal("650000"));
        acneTreatment.setDuration(75);
        acneTreatment.setCategory(acneCategory);
        acneTreatment.setStatus("ACTIVE");
        acneTreatment.setFeatured(false);
        
        // Thêm vào danh sách
        services.add(basicFacial);
        services.add(advancedFacial);
        services.add(bodyScrub);
        services.add(acneTreatment);
    }
    
    // Phương thức lấy tất cả dịch vụ đang hoạt động
    public List<com.skincare.model.Service> getAllActiveServices() {
        return services.stream()
                .filter(service -> "ACTIVE".equals(service.getStatus()))
                .collect(Collectors.toList());
    }
    
    // Phương thức lấy dịch vụ theo ID
    public Optional<com.skincare.model.Service> getServiceById(Long id) {
        return services.stream()
                .filter(service -> service.getId().equals(id))
                .findFirst();
    }
    
    // Phương thức lấy danh sách dịch vụ nổi bật
    public List<com.skincare.model.Service> getFeaturedServices() {
        return services.stream()
                .filter(service -> "ACTIVE".equals(service.getStatus()) && service.isFeatured())
                .collect(Collectors.toList());
    }
    
    // Phương thức lấy danh sách dịch vụ theo danh mục
    public List<com.skincare.model.Service> getServicesByCategory(Long categoryId) {
        return services.stream()
                .filter(service -> service.getCategory().getId().equals(categoryId) && "ACTIVE".equals(service.getStatus()))
                .collect(Collectors.toList());
    }
    
    // Phương thức lưu hoặc cập nhật dịch vụ
    public com.skincare.model.Service saveService(com.skincare.model.Service service) {
        if (service.getId() == null) {
            Long newId = services.stream()
                    .mapToLong(com.skincare.model.Service::getId)
                    .max()
                    .orElse(0L) + 1;
            service.setId(newId);
            services.add(service);
        } else {
            for (int i = 0; i < services.size(); i++) {
                if (services.get(i).getId().equals(service.getId())) {
                    services.set(i, service);
                    break;
                }
            }
        }
        return service;
    }
    
    // Phương thức xóa dịch vụ
    public void deleteService(Long id) {
        services.removeIf(service -> service.getId().equals(id));
    }
    
    // Phương thức tìm kiếm dịch vụ theo tên
    public List<com.skincare.model.Service> searchServices(String keyword) {
        return services.stream()
                .filter(service -> 
                    service.getName().toLowerCase().contains(keyword.toLowerCase()) &&
                    "ACTIVE".equals(service.getStatus())
                )
                .collect(Collectors.toList());
    }
    
    // Phương thức lấy danh sách tất cả danh mục
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        services.stream()
                .map(com.skincare.model.Service::getCategory)
                .distinct()
                .forEach(categories::add);
        return categories;
    }
} 