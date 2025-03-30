package com.skincare.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.skincare.model.Appointment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class QRCodeService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");

    public byte[] generateAppointmentQRCode(Appointment appointment) {
        try {
            String calendarEvent = generateCalendarEvent(appointment);
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(calendarEvent, BarcodeFormat.QR_CODE, 200, 200);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateCalendarEvent(Appointment appointment) {
        // Tạo chuỗi iCalendar cho lịch hẹn
        StringBuilder calendar = new StringBuilder();
        calendar.append("BEGIN:VCALENDAR\n");
        calendar.append("VERSION:2.0\n");
        calendar.append("BEGIN:VEVENT\n");
        
        // Thời gian bắt đầu
        calendar.append("DTSTART:")
               .append(appointment.getAppointmentDateTime().format(formatter))
               .append("\n");
        
        // Thời gian kết thúc (tính dựa trên tổng thời gian của các dịch vụ)
        int totalDuration = appointment.getServices().stream()
                                    .mapToInt(service -> service.getDuration())
                                    .sum();
        calendar.append("DTEND:")
               .append(appointment.getAppointmentDateTime().plusMinutes(totalDuration).format(formatter))
               .append("\n");
        
        // Tiêu đề
        calendar.append("SUMMARY:Lịch hẹn - Trung tâm Chăm sóc Da\n");
        
        // Mô tả
        StringBuilder description = new StringBuilder();
        description.append("Dịch vụ:\\n");
        appointment.getServices().forEach(service ->
            description.append("- ").append(service.getName())
                      .append(" (").append(service.getDuration()).append(" phút)\\n")
        );
        if (appointment.getSpecialist() != null) {
            description.append("\\nChuyên viên: ").append(appointment.getSpecialist().getFullName());
        }
        calendar.append("DESCRIPTION:").append(description.toString()).append("\n");
        
        // Địa điểm
        calendar.append("LOCATION:123 Đường ABC, Quận XYZ, TP.HCM\n");
        
        // Nhắc nhở trước 30 phút
        calendar.append("BEGIN:VALARM\n");
        calendar.append("TRIGGER:-PT30M\n");
        calendar.append("ACTION:DISPLAY\n");
        calendar.append("DESCRIPTION:Sắp đến giờ hẹn tại Trung tâm Chăm sóc Da\n");
        calendar.append("END:VALARM\n");
        
        calendar.append("END:VEVENT\n");
        calendar.append("END:VCALENDAR");
        
        return calendar.toString();
    }
} 