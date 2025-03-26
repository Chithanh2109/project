package com.skincare.service;

import com.skincare.model.SkinCareService;
import com.skincare.model.SkinCareService.ServiceCategory;
import com.skincare.model.SkinConcern;
import com.skincare.model.SkinType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface cho việc quản lý dịch vụ chăm sóc da
 */
public interface SkinCareServiceService {
    
    /**
     * Lấy tất cả dịch vụ đang hoạt động
     */
    List<SkinCareService> getAllActiveServices();
    
    /**
     * Lấy tất cả dịch vụ (bao gồm cả không hoạt động)
     */
    List<SkinCareService> getAllServices();
    
    /**
     * Lấy dịch vụ theo ID
     */
    Optional<SkinCareService> getServiceById(Long id);
    
    /**
     * Tìm kiếm dịch vụ theo tên
     */
    List<SkinCareService> searchServicesByName(String keyword);
    
    /**
     * Lấy dịch vụ theo danh mục
     */
    List<SkinCareService> getServicesByCategory(ServiceCategory category);
    
    /**
     * Lấy dịch vụ trong khoảng giá
     */
    List<SkinCareService> getServicesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Lấy dịch vụ phù hợp với loại da
     */
    List<SkinCareService> getServicesBySkinType(SkinType skinType);
    
    /**
     * Lấy dịch vụ cho vấn đề da cụ thể
     */
    List<SkinCareService> getServicesBySkinConcern(SkinConcern skinConcern);
    
    /**
     * Lấy các dịch vụ được đặt nhiều nhất
     */
    List<SkinCareService> getMostPopularServices(int limit);
    
    /**
     * Lấy các dịch vụ được đánh giá cao nhất
     */
    List<SkinCareService> getTopRatedServices(int limit);
    
    /**
     * Tạo dịch vụ mới
     */
    SkinCareService createService(SkinCareService service);
    
    /**
     * Cập nhật dịch vụ
     */
    SkinCareService updateService(SkinCareService service);
    
    /**
     * Ẩn/vô hiệu hóa dịch vụ
     */
    SkinCareService deactivateService(Long id);
    
    /**
     * Kích hoạt dịch vụ
     */
    SkinCareService activateService(Long id);
    
    /**
     * Tìm kiếm dịch vụ theo nhiều điều kiện
     */
    List<SkinCareService> searchServices(String keyword, ServiceCategory category, 
                                      BigDecimal minPrice, BigDecimal maxPrice, 
                                      SkinType skinType, SkinConcern skinConcern);
    
    /**
     * Đề xuất dịch vụ phù hợp cho người dùng
     */
    List<SkinCareService> recommendServicesForUser(Long userId, int limit);
    
    /**
     * Xóa dịch vụ
     */
    void deleteService(Long id);
} 