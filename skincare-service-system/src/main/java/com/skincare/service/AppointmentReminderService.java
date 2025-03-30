package com.skincare.service;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentReminderService {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Scheduled(cron = "0 0 20 * * *") // Gửi nhắc nhở vào 20:00 mỗi ngày
    public void sendDayBeforeReminders() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0);
        LocalDateTime dayAfterTomorrow = tomorrow.plusDays(1);
        
        List<Appointment> appointments = appointmentRepository.findAppointmentsInDateRange(tomorrow, dayAfterTomorrow);
        
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
                sendReminderEmail(appointment);
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *") // Gửi nhắc nhở vào 8:00 mỗi ngày
    public void sendSameDayReminders() {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime tomorrow = today.plusDays(1);
        
        List<Appointment> appointments = appointmentRepository.findAppointmentsInDateRange(today, tomorrow);
        
        for (Appointment appointment : appointments) {
            if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
                sendReminderEmail(appointment);
            }
        }
    }

    private void sendReminderEmail(Appointment appointment) {
        String appointmentDetails = buildAppointmentDetails(appointment);
        
        emailService.sendAppointmentReminder(
            appointment.getCustomer().getEmail(),
            appointment.getCustomer().getFullName(),
            appointmentDetails
        );
    }

    private String buildAppointmentDetails(Appointment appointment) {
        StringBuilder details = new StringBuilder();
        details.append("<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px;'>");
        
        // Thời gian
        details.append("<p><strong>Thời gian:</strong> ")
              .append(appointment.getAppointmentDateTime().format(formatter))
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