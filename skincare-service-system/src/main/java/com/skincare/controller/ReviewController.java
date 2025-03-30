package com.skincare.controller;

import com.skincare.dto.ReviewDTO;
import com.skincare.model.Review;
import com.skincare.model.User;
import com.skincare.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @GetMapping("/appointment/{appointmentId}")
    public String showReviewForm(@PathVariable Long appointmentId, Model model) {
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("reviewDTO", new ReviewDTO());
        return "review-form";
    }

    @PostMapping("/appointment/{appointmentId}")
    public String submitReview(
            @PathVariable Long appointmentId,
            @Valid @ModelAttribute ReviewDTO reviewDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal User currentUser,
            Model model) {
        
        if (bindingResult.hasErrors()) {
            return "review-form";
        }

        try {
            reviewService.createReview(appointmentId, reviewDTO, currentUser.getId());
            return "redirect:/appointments/my-appointments?reviewSuccess=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "review-form";
        }
    }

    @GetMapping("/specialist")
    public String showSpecialistReviews(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        
        Page<Review> reviews = reviewService.getSpecialistReviews(currentUser.getId(), pageable);
        Map<String, Object> statistics = reviewService.getSpecialistStatistics(currentUser.getId());
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("statistics", statistics);
        
        return "specialist-reviews";
    }

    @GetMapping("/my-reviews")
    public String showCustomerReviews(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        
        Page<Review> reviews = reviewService.getCustomerReviews(currentUser.getId(), pageable);
        model.addAttribute("reviews", reviews.map(this::convertToDTO));
        
        return "customer-reviews";
    }

    @PostMapping("/{reviewId}/respond")
    @ResponseBody
    public ResponseEntity<?> respondToReview(
            @PathVariable Long reviewId,
            @RequestParam String response,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Review review = reviewService.respondToReview(reviewId, response, currentUser.getId());
            return ResponseEntity.ok(convertToDTO(review));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/testimonials")
    @ResponseBody
    public ResponseEntity<?> getTestimonials(@RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(
            reviewService.getRandomPositiveReviews(limit).stream()
                .map(this::convertToDTO)
                .toList()
        );
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setSpecialistResponse(review.getSpecialistResponse());
        dto.setCustomerName(review.getCustomer().getFullName());
        dto.setSpecialistName(review.getSpecialist() != null ? 
            review.getSpecialist().getFullName() : null);
        dto.setAppointmentDate(review.getAppointment().getAppointmentDateTime().format(formatter));
        dto.setServices(review.getAppointment().getServices().stream()
            .map(service -> service.getName())
            .reduce((a, b) -> a + ", " + b)
            .orElse(""));
        dto.setCreatedAt(review.getCreatedAt().format(formatter));
        if (review.getRespondedAt() != null) {
            dto.setRespondedAt(review.getRespondedAt().format(formatter));
        }
        return dto;
    }
} 