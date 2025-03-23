package com.skincare.repository;

import com.skincare.dto.ReviewAnalyticsDTO;
import com.skincare.model.Review;
import com.skincare.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    @Query("SELECT r FROM Review r WHERE r.appointment.id = :appointmentId")
    Review findByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT r FROM Review r WHERE r.specialist.id = :specialistId")
    Page<Review> findBySpecialistId(@Param("specialistId") Long specialistId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId")
    Page<Review> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.specialist.id = :specialistId")
    Double getAverageRatingForSpecialist(@Param("specialistId") Long specialistId);
    
    @Query("SELECT r.rating as rating, COUNT(r) as count " +
           "FROM Review r WHERE r.specialist.id = :specialistId " +
           "GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionForSpecialist(@Param("specialistId") Long specialistId);
    
    @Query("SELECT r FROM Review r WHERE r.specialist.id = :specialistId " +
           "AND r.specialistResponse IS NULL")
    List<Review> findUnrespondedReviewsBySpecialist(@Param("specialistId") Long specialistId);
    
    @Query("SELECT r FROM Review r WHERE r.rating >= 4 " +
           "ORDER BY FUNCTION('RAND') LIMIT :limit")
    List<Review> findRandomPositiveReviews(@Param("limit") int limit);

    @Query("SELECT r FROM Review r " +
           "WHERE r.createdAt BETWEEN :startDate AND :endDate " +
           "AND (:specialistId IS NULL OR r.specialist.id = :specialistId) " +
           "AND (:serviceId IS NULL OR :serviceId IN (SELECT s.id FROM r.appointment.services s))")
    List<Review> findReviewsByDateRangeAndFilters(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("specialistId") Long specialistId,
        @Param("serviceId") Long serviceId);

    @Query("SELECT new com.skincare.dto.ReviewAnalyticsDTO$SpecialistPerformance(" +
           "r.specialist.id, r.specialist.fullName, " +
           "AVG(r.rating), COUNT(r), " +
           "COUNT(CASE WHEN r.specialistResponse IS NOT NULL THEN 1 END) * 100.0 / COUNT(r), " +
           "AVG(CASE WHEN r.specialistResponse IS NOT NULL " +
           "THEN FUNCTION('TIMESTAMPDIFF', HOUR, r.createdAt, r.respondedAt) END)) " +
           "FROM Review r " +
           "WHERE r.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY r.specialist.id, r.specialist.fullName")
    List<ReviewAnalyticsDTO.SpecialistPerformance> findSpecialistPerformance(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.skincare.dto.ReviewAnalyticsDTO$ServiceRating(" +
           "s.id, s.name, " +
           "AVG(r.rating), COUNT(r), " +
           "FUNCTION('JSON_OBJECT', " +
           "'1', COUNT(CASE WHEN r.rating = 1 THEN 1 END), " +
           "'2', COUNT(CASE WHEN r.rating = 2 THEN 1 END), " +
           "'3', COUNT(CASE WHEN r.rating = 3 THEN 1 END), " +
           "'4', COUNT(CASE WHEN r.rating = 4 THEN 1 END), " +
           "'5', COUNT(CASE WHEN r.rating = 5 THEN 1 END))) " +
           "FROM Review r JOIN r.appointment.services s " +
           "WHERE r.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY s.id, s.name")
    List<ReviewAnalyticsDTO.ServiceRating> findServiceRatings(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Review r WHERE " +
           "(:status IS NULL OR " +
           "(CASE " +
           "WHEN :status = 'pending' THEN r.specialistResponse IS NULL AND r.flagged = false " +
           "WHEN :status = 'responded' THEN r.specialistResponse IS NOT NULL " +
           "WHEN :status = 'flagged' THEN r.flagged = true " +
           "END)) " +
           "AND (:rating IS NULL OR r.rating = :rating) " +
           "AND (:specialistId IS NULL OR r.specialist.id = :specialistId) " +
           "AND (:serviceId IS NULL OR :serviceId IN (SELECT s.id FROM r.services s))")
    Page<Review> findByFilters(
            @Param("status") String status,
            @Param("rating") Integer rating,
            @Param("specialistId") Long specialistId,
            @Param("serviceId") Long serviceId,
            Pageable pageable
    );

    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getAverageRating();

    @Query("SELECT r.rating as rating, COUNT(r) as count FROM Review r GROUP BY r.rating")
    Map<Integer, Long> getRatingDistribution();

    long countBySpecialistResponseIsNotNull();

    boolean existsByCustomerId(Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId ORDER BY r.createdAt DESC")
    Page<Review> findByCustomerIdOrderByCreatedAtDesc(
            @Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId AND r.rating >= :rating")
    List<Review> findByCustomerIdAndRatingGreaterThanEqual(
            @Param("customerId") Long customerId, @Param("rating") int rating);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.customer.id = :customerId")
    Double getAverageRatingByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.customer.id = :customerId")
    Integer getTotalReviewsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId AND r.rating = 5")
    List<Review> findFiveStarReviewsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId AND r.rating = 4")
    List<Review> findFourStarReviewsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId AND r.rating = 3")
    List<Review> findThreeStarReviewsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId AND r.rating = 2")
    List<Review> findTwoStarReviewsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId AND r.rating = 1")
    List<Review> findOneStarReviewsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId AND r.specialistResponse IS NOT NULL")
    List<Review> findRespondedReviewsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId AND r.specialistResponse IS NULL")
    List<Review> findUnrespondedReviewsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId ORDER BY r.createdAt DESC")
    Page<Map<String, Object>> findCustomerReviews(@Param("customerId") Long customerId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r")
    Double getAverageCustomerRating();
} 