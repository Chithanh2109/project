package com.skincare.service;

import com.skincare.dto.ReviewDTO;
import com.skincare.exception.ResourceNotFoundException;
import com.skincare.exception.ValidationException;
import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.model.Review;
import com.skincare.model.User;
import com.skincare.repository.AppointmentRepository;
import com.skincare.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    @Transactional
    public Review createReview(Long appointmentId, ReviewDTO reviewDTO, Long customerId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Lịch hẹn không tồn tại"));
        
        // Kiểm tra xem lịch hẹn đã hoàn thành chưa
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ValidationException("Chỉ có thể đánh giá sau khi hoàn thành dịch vụ");
        }
        
        // Kiểm tra xem khách hàng có phải là người đặt lịch không
        if (!appointment.getCustomer().getId().equals(customerId)) {
            throw new ValidationException("Bạn không có quyền đánh giá lịch hẹn này");
        }
        
        // Kiểm tra xem đã đánh giá chưa
        if (reviewRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new ValidationException("Bạn đã đánh giá lịch hẹn này rồi");
        }
        
        Review review = new Review();
        review.setAppointment(appointment);
        review.setCustomer(appointment.getCustomer());
        review.setSpecialist(appointment.getSpecialist());
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        
        review = reviewRepository.save(review);
        
        // Gửi thông báo cho chuyên viên
        if (appointment.getSpecialist() != null) {
            notifySpecialistOfNewReview(review);
        }
        
        return review;
    }

    @Transactional
    public Review respondToReview(Long reviewId, String response, Long specialistId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Đánh giá không tồn tại"));
        
        // Kiểm tra xem chuyên viên có phải là người được đánh giá không
        if (!review.getSpecialist().getId().equals(specialistId)) {
            throw new ValidationException("Bạn không có quyền trả lời đánh giá này");
        }
        
        review.setSpecialistResponse(response);
        review.setRespondedAt(LocalDateTime.now());
        
        review = reviewRepository.save(review);
        
        // Gửi thông báo cho khách hàng
        notifyCustomerOfResponse(review);
        
        return review;
    }

    public Page<Review> getSpecialistReviews(Long specialistId, Pageable pageable) {
        User specialist = new User();
        specialist.setId(specialistId);
        return reviewRepository.findBySpecialist(specialist, pageable);
    }

    public Map<String, Object> getSpecialistStatistics(Long specialistId) {
        User specialist = new User();
        specialist.setId(specialistId);
        
        Map<String, Object> statistics = new HashMap<>();
        
        // Tính điểm trung bình
        Double averageRating = reviewRepository.getAverageRatingForSpecialist(specialist);
        statistics.put("averageRating", averageRating != null ? averageRating : 0.0);
        
        // Phân bố số sao
        List<Object[]> distribution = reviewRepository.getRatingDistributionForSpecialist(specialist);
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        for (Object[] row : distribution) {
            ratingDistribution.put((Integer) row[0], (Long) row[1]);
        }
        statistics.put("ratingDistribution", ratingDistribution);
        
        return statistics;
    }

    public List<Review> getRandomPositiveReviews(int limit) {
        return reviewRepository.findRandomPositiveReviews(limit);
    }

    private void notifySpecialistOfNewReview(Review review) {
        String subject = "Bạn có đánh giá mới từ khách hàng";
        String content = String.format(
            "Khách hàng %s đã đánh giá dịch vụ của bạn:\n" +
            "Số sao: %d/5\n" +
            "Nhận xét: %s\n\n" +
            "Vui lòng đăng nhập để xem chi tiết và phản hồi.",
            review.getCustomer().getFullName(),
            review.getRating(),
            review.getComment()
        );
        
        emailService.sendEmail(
            review.getSpecialist().getEmail(),
            subject,
            content,
            null
        );
    }

    private void notifyCustomerOfResponse(Review review) {
        String subject = "Chuyên viên đã phản hồi đánh giá của bạn";
        String content = String.format(
            "Chuyên viên %s đã phản hồi đánh giá của bạn:\n\n" +
            "Đánh giá của bạn:\n" +
            "Số sao: %d/5\n" +
            "Nhận xét: %s\n\n" +
            "Phản hồi của chuyên viên:\n%s",
            review.getSpecialist().getFullName(),
            review.getRating(),
            review.getComment(),
            review.getSpecialistResponse()
        );
        
        emailService.sendEmail(
            review.getCustomer().getEmail(),
            subject,
            content,
            null
        );
    }
} 