package com.skincare.service.impl;

import com.skincare.model.Review;
import com.skincare.model.SkinCareService;
import com.skincare.model.User;
import com.skincare.repository.ReviewRepository;
import com.skincare.repository.SkinCareServiceRepository;
import com.skincare.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final SkinCareServiceRepository serviceRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, SkinCareServiceRepository serviceRepository) {
        this.reviewRepository = reviewRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    @Transactional
    public Review createReview(Review review) {
        // Validation can be added here
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReview(Review review) {
        // Check if review exists
        if (!reviewRepository.existsById(review.getId())) {
            throw new IllegalArgumentException("Review not found with id: " + review.getId());
        }
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Review approveReview(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Review not found with id: " + id));
        
        review.setApproved(true);
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void rejectReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> getReviewsByUser(User user) {
        return reviewRepository.findByUser(user);
    }

    @Override
    public List<Review> getReviewsByService(SkinCareService service) {
        return reviewRepository.findByService(service);
    }

    @Override
    public List<Review> getApprovedReviewsByService(SkinCareService service) {
        return reviewRepository.findByServiceAndApprovedTrue(service);
    }

    @Override
    public List<Review> getPendingReviews() {
        return reviewRepository.findByApprovedFalse();
    }

    @Override
    public Double getAverageRatingForService(Long serviceId) {
        return reviewRepository.calculateAverageRating(
            serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with id: " + serviceId))
        );
    }

    @Override
    public Long getReviewCountForService(Long serviceId) {
        return reviewRepository.countApprovedReviews(
            serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with id: " + serviceId))
        );
    }

    @Override
    public Map<Integer, Long> getRatingDistributionForService(Long serviceId) {
        SkinCareService service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new IllegalArgumentException("Service not found with id: " + serviceId));
            
        List<Review> reviews = reviewRepository.findByServiceAndApprovedTrue(service);
        
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            long count = reviews.stream()
                .filter(review -> review.getRating() == rating)
                .count();
            distribution.put(rating, count);
        }
        
        return distribution;
    }

    @Override
    public List<Review> getLatestApprovedReviews(int limit) {
        return reviewRepository.findAll().stream()
            .filter(Review::isApproved)
            .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> getFeaturedReviews(int limit) {
        return reviewRepository.findAll().stream()
            .filter(Review::isApproved)
            .filter(review -> review.getRating() >= 4) // Featured reviews have high ratings
            .sorted(Comparator.comparing(Review::getRating).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<Review> searchReviews(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowercaseKeyword = keyword.toLowerCase();
        
        // Search in review comments
        return reviewRepository.findAll().stream()
            .filter(Review::isApproved)
            .filter(review -> 
                review.getComment() != null && 
                review.getComment().toLowerCase().contains(lowercaseKeyword))
            .collect(Collectors.toList());
    }
} 