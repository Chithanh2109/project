package com.skincare.service;

import com.skincare.model.Category;
import com.skincare.model.Service;
import com.skincare.repository.CategoryRepository;
import com.skincare.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public ServiceService(ServiceRepository serviceRepository, CategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }
    
    // Tạm thời dùng dữ liệu giả cho đến khi tích hợp với CSDL
    private List<Service> services = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    
    public ServiceService() {
        // Khởi tạo một số dữ liệu mẫu
        initSampleData();
    }
    
    private void initSampleData() {
        // Tạo danh mục
        Category facialCategory = new Category();
        facialCategory.setId(1L);
        facialCategory.setName("Chăm sóc da mặt");
        facialCategory.setDescription("Các dịch vụ chăm sóc da mặt chuyên sâu");
        
        Category bodyCategory = new Category();
        bodyCategory.setId(2L);
        bodyCategory.setName("Chăm sóc cơ thể");
        bodyCategory.setDescription("Các dịch vụ chăm sóc da toàn thân");
        
        Category acneCategory = new Category();
        acneCategory.setId(3L);
        acneCategory.setName("Điều trị mụn");
        acneCategory.setDescription("Các dịch vụ điều trị mụn chuyên sâu");
        
        categories.add(facialCategory);
        categories.add(bodyCategory);
        categories.add(acneCategory);
        
        // Tạo dịch vụ
        Service service1 = new Service();
        service1.setId(1L);
        service1.setName("Trị liệu da mặt cao cấp");
        service1.setDescription("Phương pháp trị liệu chuyên sâu giúp làm sạch, dưỡng ẩm và cải thiện tình trạng da mặt.");
        service1.setPrice(800000.0);
        service1.setDuration(60);
        service1.setCategory(facialCategory);
        service1.setActive(true);
        
        Service service2 = new Service();
        service2.setId(2L);
        service2.setName("Điều trị mụn chuyên sâu");
        service2.setDescription("Phương pháp điều trị mụn hiệu quả, giúp loại bỏ mụn và ngăn ngừa sẹo thâm.");
        service2.setPrice(650000.0);
        service2.setDuration(90);
        service2.setCategory(acneCategory);
        service2.setActive(true);
        
        Service service3 = new Service();
        service3.setId(3L);
        service3.setName("Trẻ hóa da không phẫu thuật");
        service3.setDescription("Liệu pháp trẻ hóa da tiên tiến không xâm lấn, giúp làm săn chắc và trẻ hóa làn da.");
        service3.setPrice(1200000.0);
        service3.setDuration(120);
        service3.setCategory(facialCategory);
        service3.setActive(true);
        
        Service service4 = new Service();
        service4.setId(4L);
        service4.setName("Massage toàn thân thư giãn");
        service4.setDescription("Phương pháp massage kết hợp giữa các kỹ thuật phương Đông và phương Tây giúp thư giãn cơ thể.");
        service4.setPrice(550000.0);
        service4.setDuration(60);
        service4.setCategory(bodyCategory);
        service4.setActive(true);
        
        services.add(service1);
        services.add(service2);
        services.add(service3);
        services.add(service4);
    }
    
    public List<Service> getAllServices() {
        return services;
    }
    
    public List<Service> getAllActiveServices() {
        return services.stream()
                .filter(Service::isActive)
                .toList();
    }
    
    public List<Service> getFeaturedServices() {
        // Giả sử 3 dịch vụ đầu tiên là nổi bật
        return services.stream()
                .filter(Service::isActive)
                .limit(3)
                .toList();
    }
    
    public Optional<Service> getServiceById(Long id) {
        return services.stream()
                .filter(service -> service.getId().equals(id))
                .findFirst();
    }
    
    public List<Service> getServicesByCategory(Long categoryId) {
        return services.stream()
                .filter(service -> service.getCategory().getId().equals(categoryId))
                .filter(Service::isActive)
                .toList();
    }
    
    public List<Service> getRelatedServices(Long serviceId) {
        // Lấy dịch vụ hiện tại
        Service currentService = getServiceById(serviceId).orElse(null);
        if (currentService == null) {
            return List.of();
        }
        
        // Lấy các dịch vụ khác trong cùng danh mục
        return services.stream()
                .filter(service -> service.getCategory().getId().equals(currentService.getCategory().getId()))
                .filter(service -> !service.getId().equals(serviceId))
                .filter(Service::isActive)
                .limit(3)
                .toList();
    }
    
    public List<Category> getAllCategories() {
        return categories;
    }
    
    public Optional<Category> getCategoryById(Long id) {
        return categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst();
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
    
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }
} 