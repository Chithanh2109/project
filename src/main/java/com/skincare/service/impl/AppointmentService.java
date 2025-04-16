package com.skincare.service;

import com.skincare.dto.AppointmentDTO;
import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.model.User;
import com.skincare.repository.AppointmentRepository;
import com.skincare.repository.UserRepository;
import com.skincare.exception.ResourceNotFoundException;
import com.skincare.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsBySpecialist(Long specialistId) {
        return appointmentRepository.findBySpecialistId(specialistId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        // Validate specialist availability
        User specialist = userRepository.findById(appointmentDTO.getSpecialistId())
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        if (appointmentRepository.isSpecialistBusy(
                specialist,
                appointmentDTO.getDate(),
                appointmentDTO.getTime(),
                appointmentDTO.getTime().plusMinutes(60))) { // Assuming 60 minutes duration
            throw new RuntimeException("Specialist is busy at this time");
        }

        Appointment appointment = new Appointment();
        updateAppointmentFromDTO(appointment, appointmentDTO);
        return convertToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointment.setStatus(status);
        return convertToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentDTO assignSpecialist(Long appointmentId, Long specialistId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        User specialist = userRepository.findById(specialistId)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        // Validate specialist availability
        if (appointmentRepository.isSpecialistBusy(
                specialist,
                appointment.getDate(),
                appointment.getTime(),
                appointment.getTime().plusMinutes(60))) {
            throw new RuntimeException("Specialist is busy at this time");
        }

        appointment.setSpecialist(specialist);
        return convertToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setCustomerId(appointment.getCustomer().getId());
        dto.setCustomerName(appointment.getCustomer().getFullName());
        dto.setSpecialistId(appointment.getSpecialist() != null ? appointment.getSpecialist().getId() : null);
        dto.setSpecialistName(appointment.getSpecialist() != null ? appointment.getSpecialist().getFullName() : null);
        dto.setServiceId(appointment.getService().getId());
        dto.setServiceName(appointment.getService().getName());
        dto.setDate(appointment.getDate());
        dto.setTime(appointment.getTime());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        return dto;
    }

    private void updateAppointmentFromDTO(Appointment appointment, AppointmentDTO dto) {
        User customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        User specialist = dto.getSpecialistId() != null ? 
                userRepository.findById(dto.getSpecialistId())
                        .orElseThrow(() -> new RuntimeException("Specialist not found")) : null;
        
        appointment.setCustomer(customer);
        appointment.setSpecialist(specialist);
        appointment.setDate(dto.getDate());
        appointment.setTime(dto.getTime());
        appointment.setStatus(dto.getStatus());
        appointment.setNotes(dto.getNotes());
    }

    @Transactional
    public Appointment createAppointment(AppointmentDTO appointmentDTO, Long customerId) {
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        User specialist = null;
        if (appointmentDTO.getSpecialistId() != null) {
            specialist = userRepository.findById(appointmentDTO.getSpecialistId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialist not found"));
            
            validateSpecialistAvailability(specialist, appointmentDTO.getAppointmentDateTime(), 
                calculateTotalDuration(appointmentDTO.getServices()));
        }

        validateAppointmentDateTime(appointmentDTO.getAppointmentDateTime());

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setSpecialist(specialist);
        appointment.setServices(appointmentDTO.getServices());
        appointment.setAppointmentDateTime(appointmentDTO.getAppointmentDateTime());
        appointment.setNotes(appointmentDTO.getNotes());
        appointment.setStatus(AppointmentStatus.PENDING);

        appointment = appointmentRepository.save(appointment);

        // Send confirmation email
        sendConfirmationEmail(appointment);

        return appointment;
    }

    private void validateSpecialistAvailability(User specialist, LocalDateTime dateTime, int duration) {
        LocalDateTime endTime = dateTime.plusMinutes(duration);
        if (appointmentRepository.isSpecialistBusy(specialist, dateTime, endTime)) {
            throw new ValidationException("Specialist is not available at the selected time");
        }
    }

    private void validateAppointmentDateTime(LocalDateTime dateTime) {
        LocalTime businessStart = LocalTime.of(9, 0);
        LocalTime businessEnd = LocalTime.of(18, 0);
        
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Appointment time must be in the future");
        }
        
        LocalTime appointmentTime = dateTime.toLocalTime();
        if (appointmentTime.isBefore(businessStart) || appointmentTime.isAfter(businessEnd)) {
            throw new ValidationException("Appointment must be between 9:00 and 18:00");
        }
    }

    private int calculateTotalDuration(Set<Service> services) {
        return services.stream()
            .mapToInt(Service::getDuration)
            .sum();
    }

    private void sendConfirmationEmail(Appointment appointment) {
        String subject = "Xác nhận đặt lịch - Trung tâm Chăm sóc Da";
        String content = buildConfirmationEmailContent(appointment);
        emailService.sendEmail(appointment.getCustomer().getEmail(), subject, content);
    }

    private String buildConfirmationEmailContent(Appointment appointment) {
        StringBuilder content = new StringBuilder();
        content.append("Kính gửi ").append(appointment.getCustomer().getFullName()).append(",\n\n");
        content.append("Cảm ơn bạn đã đặt lịch tại Trung tâm Chăm sóc Da. Dưới đây là thông tin chi tiết:\n\n");
        content.append("Ngày giờ: ").append(appointment.getAppointmentDateTime()).append("\n");
        content.append("Dịch vụ: \n");
        
        appointment.getServices().forEach(service -> 
            content.append("- ").append(service.getName())
                  .append(" (").append(service.getDuration()).append(" phút)\n")
        );

        if (appointment.getSpecialist() != null) {
            content.append("\nChuyên viên: ").append(appointment.getSpecialist().getFullName());
        }

        content.append("\n\nVui lòng đến đúng giờ để được phục vụ tốt nhất.");
        content.append("\n\nTrân trọng,\nTrung tâm Chăm sóc Da");

        return content.toString();
    }

    public List<Appointment> getCustomerAppointments(Long customerId) {
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return appointmentRepository.findByCustomerOrderByAppointmentDateTimeDesc(customer);
    }

    public List<Appointment> getSpecialistAppointments(Long specialistId) {
        User specialist = userRepository.findById(specialistId)
            .orElseThrow(() -> new ResourceNotFoundException("Specialist not found"));
        return appointmentRepository.findBySpecialistOrderByAppointmentDateTimeDesc(specialist);
    }
} 