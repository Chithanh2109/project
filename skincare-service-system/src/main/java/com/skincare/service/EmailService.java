package com.skincare.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.skincare.model.Appointment;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final QRCodeService qrCodeService;
    private final SMSService smsService;

    @Async
    public void sendEmail(String to, String subject, String content, byte[] qrCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            if (qrCode != null) {
                helper.addInline("qrcode", new ByteArrayResource(qrCode), "image/png");
            }
            
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log error but don't throw exception as this is async
            e.printStackTrace();
        }
    }

    @Async
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables, byte[] qrCode) {
        try {
            Context context = new Context();
            variables.forEach(context::setVariable);
            
            if (qrCode != null) {
                context.setVariable("hasQRCode", true);
            }
            
            String content = templateEngine.process(templateName, context);
            sendEmail(to, subject, content, qrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAppointmentConfirmation(Appointment appointment) {
        String subject = "Xác nhận đặt lịch - Trung tâm Chăm sóc Da";
        String appointmentDetails = buildAppointmentDetails(appointment);
        byte[] qrCode = qrCodeService.generateAppointmentQRCode(appointment);
        
        Map<String, Object> variables = Map.of(
            "customerName", appointment.getCustomer().getFullName(),
            "appointmentDetails", appointmentDetails,
            "appointmentUrl", "/appointments/" + appointment.getId()
        );
        
        // Gửi email
        sendTemplateEmail(
            appointment.getCustomer().getEmail(),
            subject,
            "email/appointment-confirmation",
            variables,
            qrCode
        );
        
        // Gửi SMS
        smsService.sendAppointmentConfirmation(
            appointment.getCustomer().getPhoneNumber(),
            appointment.getCustomer().getFullName(),
            appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    public void sendAppointmentReminder(Appointment appointment) {
        String subject = "Nhắc nhở lịch hẹn - Trung tâm Chăm sóc Da";
        String appointmentDetails = buildAppointmentDetails(appointment);
        byte[] qrCode = qrCodeService.generateAppointmentQRCode(appointment);
        
        Map<String, Object> variables = Map.of(
            "customerName", appointment.getCustomer().getFullName(),
            "appointmentDetails", appointmentDetails,
            "appointmentUrl", "/appointments/" + appointment.getId()
        );
        
        // Gửi email
        sendTemplateEmail(
            appointment.getCustomer().getEmail(),
            subject,
            "email/appointment-reminder",
            variables,
            qrCode
        );
        
        // Gửi SMS
        smsService.sendAppointmentReminder(
            appointment.getCustomer().getPhoneNumber(),
            appointment.getCustomer().getFullName(),
            appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
    }

    public void sendAppointmentStatusUpdate(Appointment appointment) {
        String subject = "Cập nhật trạng thái lịch hẹn - Trung tâm Chăm sóc Da";
        String appointmentDetails = buildAppointmentDetails(appointment);
        
        Map<String, Object> variables = Map.of(
            "customerName", appointment.getCustomer().getFullName(),
            "status", appointment.getStatus().toString(),
            "appointmentDetails", appointmentDetails,
            "appointmentUrl", "/appointments/" + appointment.getId()
        );
        
        // Gửi email
        sendTemplateEmail(
            appointment.getCustomer().getEmail(),
            subject,
            "email/appointment-status-update",
            variables,
            null
        );
        
        // Gửi SMS
        smsService.sendStatusUpdate(
            appointment.getCustomer().getPhoneNumber(),
            appointment.getCustomer().getFullName(),
            appointment.getStatus().toString()
        );
    }

    private String buildAppointmentDetails(Appointment appointment) {
        StringBuilder details = new StringBuilder();
        details.append("<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px;'>");
        
        // Thời gian
        details.append("<p><strong>Thời gian:</strong> ")
              .append(appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
              .append("</p>");
        
        // Dịch vụ
        details.append("<p><strong>Dịch vụ:</strong></p><ul>");
        appointment.getServices().forEach(service -> 
            details.append("<li>")
                  .append(service.getName())
                  .append(" (")
                  .append(service.getDuration())
                  .append(" phút)</li>")
        );
        details.append("</ul>");
        
        // Chuyên viên
        if (appointment.getSpecialist() != null) {
            details.append("<p><strong>Chuyên viên:</strong> ")
                  .append(appointment.getSpecialist().getFullName())
                  .append("</p>");
        }
        
        details.append("</div>");
        return details.toString();
    }
} 