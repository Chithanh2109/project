package com.skincare.controller;

import com.skincare.model.Review;
import com.skincare.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review savedReview = reviewService.createReview(review);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review review) {
        review.setId(id);
        Review updatedReview = reviewService.updateReview(review);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Review> approveReview(@PathVariable Long id) {
        Review approvedReview = reviewService.approveReview(id);
        return ResponseEntity.ok(approvedReview);
    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<Void> rejectReview(@PathVariable Long id) {
        reviewService.rejectReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Review>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    @GetMapping("/service/{serviceId}/average-rating")
    public ResponseEntity<Double> getAverageRatingForService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(reviewService.getAverageRatingForService(serviceId));
    }

    @GetMapping("/service/{serviceId}/rating-distribution")
    public ResponseEntity<Map<Integer, Long>> getRatingDistributionForService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(reviewService.getRatingDistributionForService(serviceId));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<Review>> getLatestReviews(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reviewService.getLatestApprovedReviews(limit));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Review>> getFeaturedReviews(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reviewService.getFeaturedReviews(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Review>> searchReviews(@RequestParam String keyword) {
        return ResponseEntity.ok(reviewService.searchReviews(keyword));
    }
} 