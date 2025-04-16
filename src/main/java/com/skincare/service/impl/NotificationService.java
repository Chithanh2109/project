package com.skincare.service;

import com.skincare.model.Notification;
import com.skincare.model.NotificationType;
import com.skincare.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        // Gửi thông báo realtime
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/topic/notifications",
                notification
        );

        return notification;
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalse(userId);
        notifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));

        notificationRepository.delete(notification);
    }

    @Transactional
    public void deleteAllNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notificationRepository.deleteAll(notifications);
    }

    // Các phương thức tiện ích để tạo thông báo
    public void sendAppointmentConfirmation(Long userId, String appointmentDetails) {
        createNotification(
                userId,
                "Xác nhận lịch hẹn",
                "Lịch hẹn của bạn đã được xác nhận: " + appointmentDetails,
                NotificationType.APPOINTMENT
        );
    }

    public void sendAppointmentReminder(Long userId, String appointmentDetails) {
        createNotification(
                userId,
                "Nhắc nhở lịch hẹn",
                "Bạn có lịch hẹn sắp tới: " + appointmentDetails,
                NotificationType.REMINDER
        );
    }

    public void sendPaymentConfirmation(Long userId, String paymentDetails) {
        createNotification(
                userId,
                "Xác nhận thanh toán",
                "Thanh toán của bạn đã được xác nhận: " + paymentDetails,
                NotificationType.PAYMENT
        );
    }

    public void sendPromotionNotification(Long userId, String promotionDetails) {
        createNotification(
                userId,
                "Khuyến mãi mới",
                "Có khuyến mãi mới dành cho bạn: " + promotionDetails,
                NotificationType.PROMOTION
        );
    }

    public void sendSystemNotification(Long userId, String title, String message) {
        createNotification(
                userId,
                title,
                message,
                NotificationType.SYSTEM
        );
    }
} 