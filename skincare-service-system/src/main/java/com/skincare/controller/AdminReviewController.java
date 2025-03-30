package com.skincare.controller;

import com.skincare.service.ReviewService;
import com.skincare.service.SpecialistService;
import com.skincare.service.ServiceService;
import com.skincare.dto.ReviewDTO;
import com.skincare.service.ReviewAnalyticsService;
import com.skincare.dto.ReviewAnalyticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private SpecialistService specialistService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ReviewAnalyticsService reviewAnalyticsService;

    @GetMapping
    public String listReviews(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Long specialistId,
            @RequestParam(required = false) Long serviceId,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ) {
        // Thêm danh sách chuyên viên và dịch vụ cho dropdown
        model.addAttribute("specialists", specialistService.getAllSpecialists());
        model.addAttribute("services", serviceService.getAllServices());

        // Lấy danh sách đánh giá theo bộ lọc
        Page<ReviewDTO> reviews = reviewService.getFilteredReviews(status, rating, specialistId, serviceId, pageable);
        model.addAttribute("reviews", reviews);

        return "admin/reviews";
    }

    @GetMapping("/analytics")
    public String showAnalytics(
            @RequestParam(defaultValue = "30") int days,
            Model model
    ) {
        ReviewAnalyticsDTO analytics = reviewAnalyticsService.getAnalytics(days);
        model.addAttribute("analytics", analytics);
        return "admin/review-analytics";
    }

    @GetMapping("/analytics/data")
    @ResponseBody
    public ResponseEntity<ReviewAnalyticsDTO> getAnalyticsData(
            @RequestParam(defaultValue = "30") int days
    ) {
        ReviewAnalyticsDTO analytics = reviewAnalyticsService.getAnalytics(days);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/analytics/export")
    public ResponseEntity<byte[]> exportAnalytics(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(defaultValue = "pdf") String format
    ) {
        byte[] report;
        String fileName;
        String contentType;

        if ("excel".equalsIgnoreCase(format)) {
            report = reviewAnalyticsService.generateExcelReport(days);
            fileName = "review-analytics.xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else {
            report = reviewAnalyticsService.generatePdfReport(days);
            fileName = "review-analytics.pdf";
            contentType = "application/pdf";
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .body(report);
    }

    @GetMapping("/analytics/trend")
    @ResponseBody
    public ResponseEntity<?> getRatingTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(reviewAnalyticsService.getRatingTrend(startDate, endDate));
    }

    @GetMapping("/analytics/specialists")
    @ResponseBody
    public ResponseEntity<?> getSpecialistPerformance(
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(reviewAnalyticsService.getSpecialistPerformance(days));
    }

    @GetMapping("/analytics/services")
    @ResponseBody
    public ResponseEntity<?> getServiceRatings(
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(reviewAnalyticsService.getServiceRatings(days));
    }

    @PostMapping("/{id}/respond")
    public String respondToReview(@PathVariable Long id, @RequestParam String response) {
        reviewService.respondToReview(id, response);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/flag")
    @ResponseBody
    public ResponseEntity<Void> toggleFlag(@PathVariable Long id) {
        reviewService.toggleReviewFlag(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return "redirect:/admin/reviews";
    }
} 