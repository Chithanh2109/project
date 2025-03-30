package com.skincare.service;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentService;
import com.skincare.model.Service;
import com.skincare.model.User;
import com.skincare.repository.AppointmentRepository;
import com.skincare.repository.AppointmentServiceRepository;
import com.skincare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class AppointmentManagementService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final ServiceService serviceService;
    private final UserService userService;

    @Autowired
    public AppointmentManagementService(AppointmentRepository appointmentRepository,
                                      UserRepository userRepository,
                                      AppointmentServiceRepository appointmentServiceRepository,
                                      ServiceService serviceService,
                                      UserService userService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.serviceService = serviceService;
        this.userService = userService;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại: " + customerId));
        return appointmentRepository.findByCustomerOrderByAppointmentDateDesc(customer);
    }

    public List<Appointment> getAppointmentsByTherapist(Long therapistId) {
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Chuyên viên không tồn tại: " + therapistId));
        return appointmentRepository.findByTherapistOrderByAppointmentDateDesc(therapist);
    }

    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getUpcomingAppointments() {
        return appointmentRepository.findByAppointmentDateAfterAndStatusNotOrderByAppointmentDateAsc(
                LocalDateTime.now(), Appointment.AppointmentStatus.CANCELLED);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        return appointmentRepository.findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                startOfDay, endOfDay);
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Transactional
    public Appointment saveAppointment(Appointment appointment, List<Long> serviceIds) {
        if (isAppointmentTimeConflict(appointment)) {
            throw new RuntimeException("Lịch đặt bị trùng với lịch đã tồn tại. Vui lòng chọn thời gian khác.");
        }
        
        // Tính tổng thời gian cho tất cả dịch vụ
        int totalDuration = calculateTotalDuration(serviceIds);
        
        // Lưu lịch hẹn
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Lưu các dịch vụ cho lịch hẹn
        for (Long serviceId : serviceIds) {
            Service service = serviceService.getServiceById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại: " + serviceId));
            
            AppointmentService appointmentService = new AppointmentService();
            appointmentService.setAppointment(savedAppointment);
            appointmentService.setService(service);
            appointmentService.setPrice(service.getPrice());
            appointmentService.setStatus(AppointmentService.ServiceStatus.PENDING);
            
            appointmentServiceRepository.save(appointmentService);
        }
        
        // Tính lại tổng tiền
        calculateTotalAmount(savedAppointment.getId());
        
        return savedAppointment;
    }
    
    private int calculateTotalDuration(List<Long> serviceIds) {
        int totalDuration = 0;
        for (Long serviceId : serviceIds) {
            Service service = serviceService.getServiceById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại: " + serviceId));
            totalDuration += service.getDurationMinutes();
        }
        return totalDuration;
    }
    
    /**
     * Kiểm tra xem lịch đặt có bị trùng với lịch đặt khác không
     * Một lịch đặt được coi là trùng khi:
     * 1. Cùng chuyên viên trị liệu và thời gian bị chồng chéo
     * 2. Cùng khách hàng và thời gian bị chồng chéo
     */
    private boolean isAppointmentTimeConflict(Appointment appointment) {
        LocalDateTime appointmentStart = appointment.getAppointmentDate();
        
        // Tính thời gian kết thúc dựa trên tổng thời gian của các dịch vụ
        // Tạm thời dùng thời gian mặc định là 1 giờ nếu chưa có thông tin dịch vụ
        LocalDateTime appointmentEnd = appointmentStart.plusHours(1);
        
        List<Appointment> existingAppointments;
        
        // Kiểm tra trùng lịch của khách hàng
        if (appointment.getCustomer() != null) {
            existingAppointments = appointmentRepository.findByCustomerAndStatusNotAndAppointmentDateBetween(
                    appointment.getCustomer(), 
                    Appointment.AppointmentStatus.CANCELLED,
                    appointmentStart.minusHours(1),
                    appointmentEnd.plusHours(1));
            
            if (!existingAppointments.isEmpty()) {
                for (Appointment existing : existingAppointments) {
                    if (existing.getId() != null && appointment.getId() != null && existing.getId().equals(appointment.getId())) {
                        continue; // Bỏ qua lịch đặt hiện tại khi cập nhật
                    }
                    
                    LocalDateTime existingStart = existing.getAppointmentDate();
                    LocalDateTime existingEnd = existingStart.plusHours(1); // Giả sử mỗi lịch kéo dài 1 giờ
                    
                    if (appointmentStart.isBefore(existingEnd) && existingStart.isBefore(appointmentEnd)) {
                        return true; // Phát hiện trùng lịch
                    }
                }
            }
        }
        
        // Kiểm tra trùng lịch của chuyên viên
        if (appointment.getTherapist() != null) {
            existingAppointments = appointmentRepository.findByTherapistAndStatusNotAndAppointmentDateBetween(
                    appointment.getTherapist(), 
                    Appointment.AppointmentStatus.CANCELLED,
                    appointmentStart.minusHours(1),
                    appointmentEnd.plusHours(1));
            
            if (!existingAppointments.isEmpty()) {
                for (Appointment existing : existingAppointments) {
                    if (existing.getId() != null && appointment.getId() != null && existing.getId().equals(appointment.getId())) {
                        continue; // Bỏ qua lịch đặt hiện tại khi cập nhật
                    }
                    
                    LocalDateTime existingStart = existing.getAppointmentDate();
                    LocalDateTime existingEnd = existingStart.plusHours(1); // Giả sử mỗi lịch kéo dài 1 giờ
                    
                    if (appointmentStart.isBefore(existingEnd) && existingStart.isBefore(appointmentEnd)) {
                        return true; // Phát hiện trùng lịch
                    }
                }
            }
        }
        
        return false;
    }

    @Transactional
    public void calculateTotalAmount(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        java.math.BigDecimal totalAmount = appointment.getServices().stream()
                .map(AppointmentService::getPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        appointment.setTotalAmount(totalAmount);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public boolean cancelAppointment(Long id, User user) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + id));
        
        // Kiểm tra quyền hủy lịch
        boolean isAdmin = userService.isManager(user);
        boolean isCustomer = appointment.getCustomer().equals(user);
        
        if (!isAdmin && !isCustomer) {
            throw new RuntimeException("Bạn không có quyền hủy lịch hẹn này");
        }
        
        // Kiểm tra thời gian hủy (phải hủy trước 24 giờ)
        if (!isAdmin && appointment.getAppointmentDate().isBefore(LocalDateTime.now().plusHours(24))) {
            return false;
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return true;
    }

    @Transactional
    public Appointment checkIn(Long id, User staff) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + id));
        
        if (!userService.isStaffOrManager(staff)) {
            throw new RuntimeException("Chỉ nhân viên mới có thể thực hiện check-in");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.CHECKED_IN);
        appointment.setCheckinTime(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment assignTherapist(Long appointmentId, Long therapistId, User staff) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        if (!userService.isStaffOrManager(staff)) {
            throw new RuntimeException("Chỉ nhân viên mới có thể phân công chuyên viên");
        }
        
        User therapist = userService.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Chuyên viên không tồn tại: " + therapistId));
        
        if (therapist.getUserType() != User.UserType.SKIN_THERAPIST) {
            throw new RuntimeException("Người dùng được chọn không phải là chuyên viên trị liệu da");
        }
        
        // Kiểm tra trùng lịch của chuyên viên
        appointment.setTherapist(therapist);
        if (isAppointmentTimeConflict(appointment)) {
            throw new RuntimeException("Chuyên viên đã có lịch khác vào thời gian này. Vui lòng chọn chuyên viên khác.");
        }
        
        appointment.setAssignedBy(staff);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment startService(Long appointmentId, Long serviceId, User therapist) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        if (therapist.getUserType() != User.UserType.SKIN_THERAPIST) {
            throw new RuntimeException("Chỉ chuyên viên trị liệu mới có thể thực hiện dịch vụ");
        }
        
        if (!appointment.getTherapist().equals(therapist) && !userService.isManager(therapist)) {
            throw new RuntimeException("Bạn không được phân công thực hiện dịch vụ này");
        }
        
        AppointmentService appointmentService = appointment.getServices().stream()
                .filter(as -> as.getService().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại trong lịch hẹn này"));
        
        appointmentService.setStatus(AppointmentService.ServiceStatus.IN_PROGRESS);
        appointmentService.setStartTime(LocalDateTime.now());
        appointmentService.setPerformedBy(therapist);
        appointmentServiceRepository.save(appointmentService);
        
        appointment.setStatus(Appointment.AppointmentStatus.IN_PROGRESS);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment completeService(Long appointmentId, Long serviceId, String notes, User therapist) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        AppointmentService appointmentService = appointment.getServices().stream()
                .filter(as -> as.getService().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại trong lịch hẹn này"));
        
        if (!appointmentService.getPerformedBy().equals(therapist) && !userService.isManager(therapist)) {
            throw new RuntimeException("Bạn không được phân công thực hiện dịch vụ này");
        }
        
        appointmentService.setStatus(AppointmentService.ServiceStatus.COMPLETED);
        appointmentService.setEndTime(LocalDateTime.now());
        appointmentService.setNotes(notes);
        appointmentServiceRepository.save(appointmentService);
        
        // Kiểm tra xem tất cả dịch vụ đã hoàn thành chưa
        boolean allCompleted = appointment.getServices().stream()
                .allMatch(as -> as.getStatus() == AppointmentService.ServiceStatus.COMPLETED);
        
        if (allCompleted) {
            appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        }
        
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment checkOut(Long id, User staff) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + id));
        
        if (!userService.isStaffOrManager(staff)) {
            throw new RuntimeException("Chỉ nhân viên mới có thể thực hiện check-out");
        }
        
        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        appointment.setCheckoutTime(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }
} 