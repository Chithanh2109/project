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
import com.skincare.model.AppointmentStatus;
import java.time.format.DateTimeFormatter;

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
        try {
            Context context = new Context();
            context.setVariable("appointment", appointment);
            context.setVariable("formatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String content = templateEngine.process("email/appointment-confirmation", context);
            sendEmail(appointment.getCustomer().getEmail(), 
                    "Xác nhận lịch hẹn - Trung tâm Chăm sóc Da", 
                    content,
                    qrCodeService.generateAppointmentQRCode(appointment)
            );
            
            // Gửi SMS
            smsService.sendAppointmentConfirmation(
                appointment.getCustomer().getPhoneNumber(),
                appointment.getCustomer().getFullName(),
                appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendAppointmentReminder(Appointment appointment) {
        try {
            Context context = new Context();
            context.setVariable("appointment", appointment);
            context.setVariable("formatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String content = templateEngine.process("email/appointment-reminder", context);
            sendEmail(appointment.getCustomer().getEmail(), 
                    "Nhắc nhở lịch hẹn - Trung tâm Chăm sóc Da", 
                    content,
                    qrCodeService.generateAppointmentQRCode(appointment)
            );
            
            // Gửi SMS
            smsService.sendAppointmentReminder(
                appointment.getCustomer().getPhoneNumber(),
                appointment.getCustomer().getFullName(),
                appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendAppointmentStatusUpdate(Appointment appointment) {
        try {
            Context context = new Context();
            context.setVariable("appointment", appointment);
            context.setVariable("formatter", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String subject = "";
            String template = "";
            switch (appointment.getStatus()) {
                case CONFIRMED:
                    subject = "Lịch hẹn đã được xác nhận";
                    template = "email/appointment-confirmed";
                    break;
                case COMPLETED:
                    subject = "Lịch hẹn đã hoàn thành";
                    template = "email/appointment-completed";
                    break;
                case CANCELLED:
                    subject = "Lịch hẹn đã bị hủy";
                    template = "email/appointment-cancelled";
                    break;
            }

            String content = templateEngine.process(template, context);
            sendEmail(appointment.getCustomer().getEmail(), subject, content, null);
            
            // Gửi SMS
            smsService.sendStatusUpdate(
                appointment.getCustomer().getPhoneNumber(),
                appointment.getCustomer().getFullName(),
                appointment.getStatus().toString()
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
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