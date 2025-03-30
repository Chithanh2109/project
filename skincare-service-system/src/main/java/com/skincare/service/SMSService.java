package com.skincare.service;

import com.skincare.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SMSService {

    private final TwilioConfig twilioConfig;

    public void sendSMS(String to, String content) {
        try {
            Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
            
            Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(twilioConfig.getFromNumber()),
                content
            ).create();
            
            System.out.println("SMS sent with SID: " + message.getSid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAppointmentConfirmation(String to, String customerName, String appointmentDateTime) {
        String message = String.format(
            "Xin chào %s,\n" +
            "Lịch hẹn của bạn tại Trung tâm Chăm sóc Da đã được xác nhận:\n" +
            "Thời gian: %s\n" +
            "Vui lòng đến trước 10 phút để làm thủ tục.",
            customerName, appointmentDateTime
        );
        sendSMS(to, message);
    }

    public void sendAppointmentReminder(String to, String customerName, String appointmentDateTime) {
        String message = String.format(
            "Xin chào %s,\n" +
            "Nhắc nhở: Bạn có lịch hẹn tại Trung tâm Chăm sóc Da vào:\n" +
            "Thời gian: %s\n" +
            "Vui lòng đến đúng giờ. Nếu cần hủy/đổi lịch, vui lòng liên hệ (028) 1234 5678.",
            customerName, appointmentDateTime
        );
        sendSMS(to, message);
    }

    public void sendStatusUpdate(String to, String customerName, String status) {
        String message = String.format(
            "Xin chào %s,\n" +
            "Trạng thái lịch hẹn của bạn đã được cập nhật thành: %s\n" +
            "Chi tiết xem tại website hoặc email của bạn.",
            customerName, getStatusText(status)
        );
        sendSMS(to, message);
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "CONFIRMED" -> "Đã xác nhận";
            case "CANCELLED" -> "Đã hủy";
            case "COMPLETED" -> "Đã hoàn thành";
            case "NO_SHOW" -> "Khách không đến";
            default -> status;
        };
    }
} 