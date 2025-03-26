package com.skincare.service;

import com.skincare.model.Review;
import com.skincare.model.SkinCareService;
import com.skincare.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface cho việc quản lý đánh giá dịch vụ
 */
public interface ReviewService {
    
    /**
     * Lấy tất cả đánh giá
     */
    List<Review> getAllReviews();
    
    /**
     * Lấy đánh giá theo ID
     */
    Optional<Review> getReviewById(Long id);
    
    /**
     * Tạo đánh giá mới
     */
    Review createReview(Review review);
    
    /**
     * Cập nhật đánh giá
     */
    Review updateReview(Review review);
    
    /**
     * Xóa đánh giá
     */
    void deleteReview(Long id);
    
    /**
     * Phê duyệt đánh giá
     */
    Review approveReview(Long id);
    
    /**
     * Từ chối đánh giá
     */
    void rejectReview(Long id);
    
    /**
     * Lấy tất cả đánh giá của một người dùng
     */
    List<Review> getReviewsByUser(User user);
    
    /**
     * Lấy tất cả đánh giá của một dịch vụ
     */
    List<Review> getReviewsByService(SkinCareService service);
    
    /**
     * Lấy tất cả đánh giá đã phê duyệt của một dịch vụ
     */
    List<Review> getApprovedReviewsByService(SkinCareService service);
    
    /**
     * Lấy tất cả đánh giá đang chờ phê duyệt
     */
    List<Review> getPendingReviews();
    
    /**
     * Tính điểm trung bình của một dịch vụ
     */
    Double getAverageRatingForService(Long serviceId);
    
    /**
     * Lấy số lượng đánh giá của một dịch vụ
     */
    Long getReviewCountForService(Long serviceId);
    
    /**
     * Lấy phân phối các điểm đánh giá của một dịch vụ
     */
    Map<Integer, Long> getRatingDistributionForService(Long serviceId);
    
    /**
     * Lấy các đánh giá mới nhất đã được phê duyệt
     */
    List<Review> getLatestApprovedReviews(int limit);
    
    /**
     * Lấy các đánh giá nổi bật (có điểm cao hoặc nội dung hữu ích)
     */
    List<Review> getFeaturedReviews(int limit);
    
    /**
     * Tìm kiếm đánh giá theo từ khóa
     */
    List<Review> searchReviews(String keyword);
} 