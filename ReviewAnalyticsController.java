package com.skincare.controller.admin;

import com.skincare.dto.ReviewAnalyticsDTO;
import com.skincare.model.Service;
import com.skincare.model.User;
import com.skincare.service.ReviewAnalyticsService;
import com.skincare.service.ServiceService;
import com.skincare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class ReviewAnalyticsController {

    private final ReviewAnalyticsService reviewAnalyticsService;
    private final UserService userService;
    private final ServiceService serviceService;

    @GetMapping("/analytics")
    public String showAnalytics(
            @RequestParam(required = false, defaultValue = "30") Integer timeRange,
            @RequestParam(required = false) Long specialistId,
            @RequestParam(required = false) Long serviceId,
            Model model) {

        // Get specialists and services for filter dropdowns
        List<User> specialists = userService.getAllSpecialists();
        List<Service> services = serviceService.getAllServices();
        model.addAttribute("specialists", specialists);
        model.addAttribute("services", services);

        // Get analytics data
        ReviewAnalyticsDTO stats = reviewAnalyticsService.getAnalytics(timeRange, specialistId, serviceId);
        model.addAttribute("stats", stats);

        // Get chart data
        model.addAttribute("ratingTrendData", reviewAnalyticsService.getRatingTrendData(timeRange, specialistId, serviceId));
        model.addAttribute("ratingDistributionData", reviewAnalyticsService.getRatingDistributionData(timeRange, specialistId, serviceId));

        // Get recent reviews
        model.addAttribute("recentReviews", reviewAnalyticsService.getRecentReviews(10));

        return "admin/review-analytics";
    }

    @GetMapping("/analytics/export/{format}")
    public ResponseEntity<?> exportAnalytics(
            @PathVariable String format,
            @RequestParam(required = false) Integer timeRange,
            @RequestParam(required = false) Long specialistId,
            @RequestParam(required = false) Long serviceId) {

        byte[] report = reviewAnalyticsService.generateReport(format, timeRange, specialistId, serviceId);
        String filename = String.format("review-analytics-%s.%s", LocalDate.now(), format.toLowerCase());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", format.equals("pdf") ? 
                        "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(report);
    }

    @GetMapping("/analytics/api/trend")
    @ResponseBody
    public ResponseEntity<?> getRatingTrend(
            @RequestParam(required = false, defaultValue = "30") Integer timeRange,
            @RequestParam(required = false) Long specialistId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
            reviewAnalyticsService.getRatingTrendData(timeRange, specialistId, serviceId, startDate, endDate)
        );
    }

    @GetMapping("/analytics/api/distribution")
    @ResponseBody
    public ResponseEntity<?> getRatingDistribution(
            @RequestParam(required = false, defaultValue = "30") Integer timeRange,
            @RequestParam(required = false) Long specialistId,
            @RequestParam(required = false) Long serviceId) {

        return ResponseEntity.ok(
            reviewAnalyticsService.getRatingDistributionData(timeRange, specialistId, serviceId)
        );
    }

    @GetMapping("/analytics/api/specialist-performance")
    @ResponseBody
    public ResponseEntity<?> getSpecialistPerformance(
            @RequestParam(required = false, defaultValue = "30") Integer timeRange) {

        return ResponseEntity.ok(
            reviewAnalyticsService.getSpecialistPerformanceData(timeRange)
        );
    }

    @GetMapping("/analytics/api/service-ratings")
    @ResponseBody
    public ResponseEntity<?> getServiceRatings(
            @RequestParam(required = false, defaultValue = "30") Integer timeRange) {

        return ResponseEntity.ok(
            reviewAnalyticsService.getServiceRatingsData(timeRange)
        );
    }
} 