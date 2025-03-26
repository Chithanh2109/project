package com.skincare.controller;

import com.skincare.model.Review;
import com.skincare.model.SkinCareService;
import com.skincare.model.User;
import com.skincare.service.ReviewService;
import com.skincare.service.SkinCareServiceService;
import com.skincare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller xử lý các yêu cầu liên quan đến đánh giá
 */
@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final SkinCareServiceService serviceService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, 
                           SkinCareServiceService serviceService,
                           UserService userService) {
        this.reviewService = reviewService;
        this.serviceService = serviceService;
        this.userService = userService;
    }
    
    /**
     * Hiển thị form để thêm đánh giá mới cho một dịch vụ
     */
    @GetMapping("/add/{serviceId}")
    public String showAddReviewForm(@PathVariable Long serviceId, Model model) {
        Optional<SkinCareService> serviceOpt = serviceService.getServiceById(serviceId);
        if (serviceOpt.isPresent()) {
            model.addAttribute("service", serviceOpt.get());
            model.addAttribute("review", new Review());
            return "reviews/add";
        }
        return "redirect:/services?error=ServiceNotFound";
    }
    
    /**
     * Xử lý thêm mới đánh giá
     */
    @PostMapping("/add")
    public String addReview(@ModelAttribute Review review, 
                          @RequestParam Long serviceId,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        
        Optional<SkinCareService> serviceOpt = serviceService.getServiceById(serviceId);
        if (!serviceOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Dịch vụ không tồn tại");
            return "redirect:/services";
        }
        
        User currentUser = userService.findByUsername(authentication.getName());
        review.setUser(currentUser);
        review.setService(serviceOpt.get());
        
        reviewService.createReview(review);
        redirectAttributes.addFlashAttribute("success", "Đánh giá của bạn đã được gửi và đang chờ phê duyệt");
        
        return "redirect:/services/" + serviceId;
    }
    
    /**
     * Hiển thị tất cả đánh giá cho một dịch vụ
     */
    @GetMapping("/service/{serviceId}")
    public String getServiceReviews(@PathVariable Long serviceId, Model model) {
        Optional<SkinCareService> serviceOpt = serviceService.getServiceById(serviceId);
        if (serviceOpt.isPresent()) {
            SkinCareService service = serviceOpt.get();
            List<Review> reviews = reviewService.getApprovedReviewsByService(service);
            
            Double averageRating = reviewService.getAverageRatingForService(serviceId);
            Map<Integer, Long> ratingDistribution = reviewService.getRatingDistributionForService(serviceId);
            
            model.addAttribute("service", service);
            model.addAttribute("reviews", reviews);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("ratingDistribution", ratingDistribution);
            
            return "reviews/service-reviews";
        }
        return "redirect:/services?error=ServiceNotFound";
    }
    
    /**
     * Trang quản lý đánh giá (Admin)
     */
    @GetMapping("/admin/manage")
    public String manageReviews(Model model) {
        List<Review> pendingReviews = reviewService.getPendingReviews();
        List<Review> approvedReviews = reviewService.getLatestApprovedReviews(100);
        
        model.addAttribute("pendingReviews", pendingReviews);
        model.addAttribute("approvedReviews", approvedReviews);
        
        return "admin/reviews/manage";
    }
    
    /**
     * Phê duyệt đánh giá (Admin)
     */
    @PostMapping("/admin/approve/{id}")
    public String approveReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.approveReview(id);
            redirectAttributes.addFlashAttribute("success", "Đánh giá đã được phê duyệt");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể phê duyệt đánh giá: " + e.getMessage());
        }
        return "redirect:/reviews/admin/manage";
    }
    
    /**
     * Từ chối đánh giá (Admin)
     */
    @PostMapping("/admin/reject/{id}")
    public String rejectReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.rejectReview(id);
            redirectAttributes.addFlashAttribute("success", "Đánh giá đã bị từ chối");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể từ chối đánh giá: " + e.getMessage());
        }
        return "redirect:/reviews/admin/manage";
    }
    
    /**
     * Đánh dấu đánh giá là nổi bật (Admin)
     */
    @PostMapping("/admin/feature/{id}")
    public String featureReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.getReviewById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đánh giá không tồn tại"));
            
            review.setFeatured(true);
            reviewService.updateReview(review);
            redirectAttributes.addFlashAttribute("success", "Đánh giá đã được đánh dấu là nổi bật");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể đánh dấu đánh giá: " + e.getMessage());
        }
        return "redirect:/reviews/admin/manage";
    }
    
    /**
     * ======== REST API ========
     */
    
    /**
     * API để lấy tất cả đánh giá
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
    
    /**
     * API để lấy đánh giá theo ID
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * API để thêm đánh giá mới
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review savedReview = reviewService.createReview(review);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }
    
    /**
     * API để cập nhật đánh giá
     */
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review review) {
        review.setId(id);
        Review updatedReview = reviewService.updateReview(review);
        return ResponseEntity.ok(updatedReview);
    }
    
    /**
     * API để xóa đánh giá
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * API để lấy trung bình đánh giá cho một dịch vụ
     */
    @GetMapping("/api/service/{serviceId}/average-rating")
    @ResponseBody
    public ResponseEntity<Double> getAverageRatingForService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(reviewService.getAverageRatingForService(serviceId));
    }
    
    /**
     * API để lấy phân phối đánh giá cho một dịch vụ
     */
    @GetMapping("/api/service/{serviceId}/rating-distribution")
    @ResponseBody
    public ResponseEntity<Map<Integer, Long>> getRatingDistributionForService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(reviewService.getRatingDistributionForService(serviceId));
    }
    
    /**
     * API để lấy đánh giá mới nhất đã phê duyệt
     */
    @GetMapping("/api/latest")
    @ResponseBody
    public ResponseEntity<List<Review>> getLatestReviews(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reviewService.getLatestApprovedReviews(limit));
    }
    
    /**
     * API để lấy đánh giá nổi bật
     */
    @GetMapping("/api/featured")
    @ResponseBody
    public ResponseEntity<List<Review>> getFeaturedReviews(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reviewService.getFeaturedReviews(limit));
    }
    
    /**
     * API để tìm kiếm đánh giá
     */
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<Review>> searchReviews(@RequestParam String keyword) {
        return ResponseEntity.ok(reviewService.searchReviews(keyword));
    }
} 