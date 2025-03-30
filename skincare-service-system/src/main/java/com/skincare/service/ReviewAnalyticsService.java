package com.skincare.service;

import com.skincare.dto.*;
import com.skincare.repository.ReviewRepository;
import com.skincare.repository.SpecialistRepository;
import com.skincare.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReviewAnalyticsService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public ReviewAnalyticsDTO getAnalytics(int days) {
        ReviewAnalyticsDTO analytics = new ReviewAnalyticsDTO();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        LocalDateTime previousStartDate = startDate.minusDays(days);

        // Tính toán thống kê hiện tại
        analytics.setTotalReviews(reviewRepository.countByCreatedAtBetween(startDate, endDate));
        analytics.setAverageRating(reviewRepository.getAverageRatingBetween(startDate, endDate));
        analytics.setResponseRate(calculateResponseRate(startDate, endDate));
        analytics.setAvgResponseTime(calculateAverageResponseTime(startDate, endDate));

        // Tính toán tỷ lệ tăng trưởng
        long previousReviews = reviewRepository.countByCreatedAtBetween(previousStartDate, startDate);
        double previousRating = reviewRepository.getAverageRatingBetween(previousStartDate, startDate);
        double previousResponseRate = calculateResponseRate(previousStartDate, startDate);
        double previousResponseTime = calculateAverageResponseTime(previousStartDate, startDate);

        analytics.setReviewsGrowth(calculateGrowth(previousReviews, analytics.getTotalReviews()));
        analytics.setRatingGrowth(calculateGrowth(previousRating, analytics.getAverageRating()));
        analytics.setResponseRateGrowth(calculateGrowth(previousResponseRate, analytics.getResponseRate()));
        analytics.setResponseTimeGrowth(calculateGrowth(previousResponseTime, analytics.getAvgResponseTime()));

        // Thêm dữ liệu xu hướng và phân bố
        analytics.setRatingTrend(getRatingTrend(startDate.toLocalDate(), endDate.toLocalDate()));
        analytics.setRatingDistribution(reviewRepository.getRatingDistributionBetween(startDate, endDate));

        // Thêm hiệu suất chuyên viên và dịch vụ
        analytics.setSpecialistPerformance(getSpecialistPerformance(days));
        analytics.setServiceRatings(getServiceRatings(days));

        return analytics;
    }

    public List<RatingTrendPointDTO> getRatingTrend(LocalDate startDate, LocalDate endDate) {
        List<RatingTrendPointDTO> trend = new ArrayList<>();
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            LocalDateTime dayStart = currentDate.atStartOfDay();
            LocalDateTime dayEnd = currentDate.plusDays(1).atStartOfDay();
            
            RatingTrendPointDTO point = new RatingTrendPointDTO();
            point.setDate(currentDate);
            point.setAverageRating(reviewRepository.getAverageRatingBetween(dayStart, dayEnd));
            point.setTotalReviews(reviewRepository.countByCreatedAtBetween(dayStart, dayEnd));
            
            trend.add(point);
            currentDate = currentDate.plusDays(1);
        }
        
        return trend;
    }

    public List<SpecialistPerformanceDTO> getSpecialistPerformance(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        return specialistRepository.findAll().stream()
            .map(specialist -> {
                SpecialistPerformanceDTO performance = new SpecialistPerformanceDTO();
                performance.setSpecialistName(specialist.getFullName());
                performance.setAverageRating(reviewRepository.getAverageRatingForSpecialist(
                    specialist.getId(), startDate, endDate));
                performance.setTotalReviews(reviewRepository.countBySpecialistAndCreatedAtBetween(
                    specialist.getId(), startDate, endDate));
                performance.setResponseRate(calculateSpecialistResponseRate(
                    specialist.getId(), startDate, endDate));
                performance.setAvgResponseTime(calculateSpecialistResponseTime(
                    specialist.getId(), startDate, endDate));
                return performance;
            })
            .collect(Collectors.toList());
    }

    public List<ServiceRatingDTO> getServiceRatings(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        return serviceRepository.findAll().stream()
            .map(service -> {
                ServiceRatingDTO rating = new ServiceRatingDTO();
                rating.setServiceName(service.getName());
                rating.setAverageRating(reviewRepository.getAverageRatingForService(
                    service.getId(), startDate, endDate));
                rating.setTotalReviews(reviewRepository.countByServiceAndCreatedAtBetween(
                    service.getId(), startDate, endDate));
                rating.setRatingDistribution(reviewRepository.getRatingDistributionForService(
                    service.getId(), startDate, endDate));
                return rating;
            })
            .collect(Collectors.toList());
    }

    public byte[] generatePdfReport(int days) {
        // TODO: Implement PDF report generation
        return new byte[0];
    }

    public byte[] generateExcelReport(int days) {
        // TODO: Implement Excel report generation
        return new byte[0];
    }

    private double calculateResponseRate(LocalDateTime startDate, LocalDateTime endDate) {
        long totalReviews = reviewRepository.countByCreatedAtBetween(startDate, endDate);
        if (totalReviews == 0) return 0.0;
        
        long respondedReviews = reviewRepository.countByRespondedAtBetweenAndSpecialistResponseIsNotNull(
            startDate, endDate);
        return (double) respondedReviews / totalReviews * 100;
    }

    private double calculateAverageResponseTime(LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.getAverageResponseTimeBetween(startDate, endDate);
    }

    private double calculateGrowth(double previous, double current) {
        if (previous == 0) return 0.0;
        return ((current - previous) / previous) * 100;
    }

    private double calculateSpecialistResponseRate(Long specialistId, LocalDateTime startDate, LocalDateTime endDate) {
        long totalReviews = reviewRepository.countBySpecialistAndCreatedAtBetween(specialistId, startDate, endDate);
        if (totalReviews == 0) return 0.0;
        
        long respondedReviews = reviewRepository.countBySpecialistAndRespondedAtBetweenAndSpecialistResponseIsNotNull(
            specialistId, startDate, endDate);
        return (double) respondedReviews / totalReviews * 100;
    }

    private double calculateSpecialistResponseTime(Long specialistId, LocalDateTime startDate, LocalDateTime endDate) {
        return reviewRepository.getAverageResponseTimeForSpecialist(specialistId, startDate, endDate);
    }
} 