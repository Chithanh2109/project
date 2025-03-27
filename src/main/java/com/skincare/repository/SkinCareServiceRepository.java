package com.skincare.repository;

import com.skincare.model.SkinCareService;
import com.skincare.model.SkinCareService.ServiceCategory;
import com.skincare.model.SkinConcern;
import com.skincare.model.SkinType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository cho thao tác với dữ liệu SkinCareService
 */
@Repository
public interface SkinCareServiceRepository extends JpaRepository<SkinCareService, Long> {
    
    /**
     * Tìm tất cả dịch vụ đang hoạt động
     */
    List<SkinCareService> findByActiveTrue();
    
    /**
     * Tìm dịch vụ theo tên chứa từ khóa
     */
    List<SkinCareService> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);
    
    /**
     * Tìm dịch vụ theo danh mục
     */
    List<SkinCareService> findByCategoryAndActiveTrue(ServiceCategory category);
    
    /**
     * Tìm dịch vụ theo khoảng giá
     */
    List<SkinCareService> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Tìm dịch vụ theo loại da phù hợp
     * Sử dụng Native Query để tìm các dịch vụ có loại da trong bảng service_skin_types
     */
    @Query(value = "SELECT s.* FROM skin_services s " +
            "JOIN service_skin_types st ON s.id = st.service_id " +
            "WHERE st.skin_type = ?1 AND s.active = true", 
            nativeQuery = true)
    List<SkinCareService> findBySuitableSkinType(String skinType);
    
    /**
     * Tìm dịch vụ theo vấn đề da cần điều trị
     * Sử dụng Native Query để tìm các dịch vụ có skin concern trong bảng service_skin_concerns
     */
    @Query(value = "SELECT s.* FROM skin_services s " +
            "JOIN service_skin_concerns sc ON s.id = sc.service_id " +
            "WHERE sc.skin_concern = ?1 AND s.active = true", 
            nativeQuery = true)
    List<SkinCareService> findBySkinConcern(String skinConcern);
    
    /**
     * Tìm các dịch vụ được đặt nhiều nhất
     * Sử dụng Native Query để join với bảng appointments và đếm số lượng
     */
    @Query(value = "SELECT s.* FROM skin_services s " +
            "JOIN appointments a ON s.id = a.service_id " +
            "WHERE s.active = true " +
            "GROUP BY s.id " +
            "ORDER BY COUNT(a.id) DESC " +
            "LIMIT :limit", 
            nativeQuery = true)
    List<SkinCareService> findMostPopularServices(@Param("limit") int limit);
    
    /**
     * Tìm các dịch vụ có đánh giá cao nhất
     * Sử dụng Native Query để join với bảng reviews và tính trung bình rating
     */
    @Query(value = "SELECT s.* FROM skin_services s " +
            "JOIN reviews r ON s.id = r.service_id " +
            "WHERE s.active = true " +
            "GROUP BY s.id " +
            "ORDER BY AVG(r.rating) DESC " +
            "LIMIT :limit", 
            nativeQuery = true)
    List<SkinCareService> findTopRatedServices(@Param("limit") int limit);
} 