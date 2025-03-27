package com.skincare.service.impl;

import com.skincare.model.SkinCareService;
import com.skincare.model.SkinCareService.ServiceCategory;
import com.skincare.model.SkinConcern;
import com.skincare.model.SkinType;
import com.skincare.repository.SkinCareServiceRepository;
import com.skincare.service.SkinCareServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Triển khai của SkinCareServiceService interface
 */
@Service
public class SkinCareServiceServiceImpl implements SkinCareServiceService {

    private final SkinCareServiceRepository serviceRepository;

    @Autowired
    public SkinCareServiceServiceImpl(SkinCareServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<SkinCareService> getAllActiveServices() {
        return serviceRepository.findByActiveTrue();
    }

    @Override
    public List<SkinCareService> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Optional<SkinCareService> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    @Override
    public List<SkinCareService> searchServicesByName(String keyword) {
        return serviceRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword);
    }

    @Override
    public List<SkinCareService> getServicesByCategory(ServiceCategory category) {
        return serviceRepository.findByCategoryAndActiveTrue(category);
    }

    @Override
    public List<SkinCareService> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return serviceRepository.findByPriceBetweenAndActiveTrue(minPrice, maxPrice);
    }

    @Override
    public List<SkinCareService> getServicesBySkinType(SkinType skinType) {
        return serviceRepository.findBySuitableSkinType(skinType.name());
    }

    @Override
    public List<SkinCareService> getServicesBySkinConcern(SkinConcern skinConcern) {
        // Triển khai dựa trên phương thức của repository nếu có
        // Ví dụ giả sử repository có một phương thức cho skin concern
        return serviceRepository.findBySkinConcern(skinConcern.name());
    }

    @Override
    public List<SkinCareService> getMostPopularServices(int limit) {
        // Triển khai dựa trên phương thức của repository
        // Ví dụ:
        return serviceRepository.findMostPopularServices(limit);
    }

    @Override
    public List<SkinCareService> getTopRatedServices(int limit) {
        // Triển khai dựa trên phương thức của repository
        // Ví dụ:
        return serviceRepository.findTopRatedServices(limit);
    }

    @Override
    @Transactional
    public SkinCareService createService(SkinCareService service) {
        // Các logic validation có thể thêm vào đây
        return serviceRepository.save(service);
    }

    @Override
    @Transactional
    public SkinCareService updateService(SkinCareService service) {
        // Kiểm tra xem dịch vụ đã tồn tại hay chưa
        if (!serviceRepository.existsById(service.getId())) {
            throw new IllegalArgumentException("Dịch vụ không tồn tại với id: " + service.getId());
        }
        return serviceRepository.save(service);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean toggleServiceActive(Long id) {
        Optional<SkinCareService> serviceOpt = serviceRepository.findById(id);
        if (serviceOpt.isPresent()) {
            SkinCareService service = serviceOpt.get();
            service.setActive(!service.isActive());
            serviceRepository.save(service);
            return service.isActive();
        }
        throw new IllegalArgumentException("Dịch vụ không tồn tại với id: " + id);
    }
} 