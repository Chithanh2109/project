package com.skincare.service;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentReview;
import com.skincare.repository.AppointmentRepository;
import com.skincare.repository.AppointmentReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentReviewService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentReviewRepository reviewRepository;
    private final EmailService emailService;

    @Transactional
    public AppointmentReview createReview(Long appointmentId, int rating, String comment) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Chỉ có thể đánh giá lịch hẹn đã hoàn thành");
        }

        if (reviewRepository.existsByAppointment(appointment)) {
            throw new RuntimeException("Lịch hẹn này đã được đánh giá");
        }

        AppointmentReview review = new AppointmentReview();
        review.setAppointment(appointment);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        // Gửi email thông báo cho admin
        emailService.sendReviewNotification(appointment, review);

        return reviewRepository.save(review);
    }

    @Transactional
    public AppointmentReview updateReview(Long reviewId, int rating, String comment) {
        AppointmentReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        review.setRating(rating);
        review.setComment(comment);
        review.setUpdatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        AppointmentReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        reviewRepository.delete(review);
    }

    public List<AppointmentReview> getAppointmentReviews(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        return reviewRepository.findByAppointmentOrderByCreatedAtDesc(appointment);
    }

    public List<AppointmentReview> getSpecialistReviews(Long specialistId) {
        return reviewRepository.findByAppointment_Specialist_IdOrderByCreatedAtDesc(specialistId);
    }

    public double getSpecialistAverageRating(Long specialistId) {
        return reviewRepository.findByAppointment_Specialist_IdOrderByCreatedAtDesc(specialistId)
                .stream()
                .mapToInt(AppointmentReview::getRating)
                .average()
                .orElse(0.0);
    }

    public List<AppointmentReview> getServiceReviews(Long serviceId) {
        return reviewRepository.findByAppointment_Service_IdOrderByCreatedAtDesc(serviceId);
    }

    public double getServiceAverageRating(Long serviceId) {
        return reviewRepository.findByAppointment_Service_IdOrderByCreatedAtDesc(serviceId)
                .stream()
                .mapToInt(AppointmentReview::getRating)
                .average()
                .orElse(0.0);
    }

    public List<AppointmentReview> getCustomerReviews(Long customerId) {
        return reviewRepository.findByAppointment_Customer_IdOrderByCreatedAtDesc(customerId);
    }

    public Optional<AppointmentReview> getReviewByAppointment(Long appointmentId) {
        return reviewRepository.findByAppointment_Id(appointmentId);
    }
} 