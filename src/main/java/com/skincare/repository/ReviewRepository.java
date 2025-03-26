package com.skincare.repository;

import com.skincare.model.Review;
import com.skincare.model.SkinCareService;
import com.skincare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho thao tác với dữ liệu Review
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Tìm tất cả đánh giá của một người dùng
     */
    List<Review> findByUser(User user);
    
    /**
     * Tìm tất cả đánh giá của một dịch vụ
     */
    List<Review> findByService(SkinCareService service);
    
    /**
     * Tìm tất cả đánh giá đã được phê duyệt của một dịch vụ
     */
    List<Review> findByServiceAndApprovedTrue(SkinCareService service);
    
    /**
     * Tìm tất cả đánh giá đang chờ phê duyệt
     */
    List<Review> findByApprovedFalse();
    
    /**
     * Tính điểm trung bình của một dịch vụ
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.service = ?1 AND r.approved = true")
    Double calculateAverageRating(SkinCareService service);
    
    /**
     * Đếm số lượng đánh giá đã được phê duyệt của một dịch vụ
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.service = ?1 AND r.approved = true")
    Long countApprovedReviews(SkinCareService service);
    
    /**
     * Tìm các đánh giá mới nhất đã được phê duyệt của một dịch vụ
     */
    @Query("SELECT r FROM Review r WHERE r.service = ?1 AND r.approved = true ORDER BY r.createdAt DESC")
    List<Review> findLatestApprovedReviews(SkinCareService service, int limit);
} 