package com.skincare.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentStatus;
import com.skincare.model.Customer;
import com.skincare.model.ServiceStatus;
import com.skincare.model.User;
import com.skincare.model.UserType;
import com.skincare.repository.AppointmentRepository;
import com.skincare.repository.AppointmentServiceRepository;
import com.skincare.repository.CustomerRepository;
import com.skincare.repository.ServiceRepository;
import com.skincare.repository.UserRepository;

@Service
public class AppointmentManagementService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final ServiceRepository serviceRepository;

    @Autowired
    public AppointmentManagementService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            AppointmentServiceRepository appointmentServiceRepository,
            ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại: " + customerId));
        return appointmentRepository.findByCustomerOrderByAppointmentDateDesc(customer);
    }

    public List<Appointment> getAppointmentsByTherapist(Long therapistId) {
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Chuyên viên không tồn tại: " + therapistId));
        return appointmentRepository.findByTherapistOrderByAppointmentDateDesc(therapist);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getUpcomingAppointments() {
        return appointmentRepository.findByAppointmentDateAfterAndStatusNotInOrderByAppointmentDateAsc(
                LocalDateTime.now(), 
                Arrays.asList(AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED, AppointmentStatus.NO_SHOW));
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
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setCreatedAt(LocalDateTime.now());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Lưu các dịch vụ cho lịch hẹn
        for (Long serviceId : serviceIds) {
            com.skincare.model.Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại: " + serviceId));
            
            com.skincare.model.AppointmentService appointmentService = new com.skincare.model.AppointmentService();
            appointmentService.setAppointment(savedAppointment);
            appointmentService.setService(service);
            appointmentService.setPrice(service.getPrice());
            appointmentService.setStatus(ServiceStatus.PENDING);
            
            appointmentServiceRepository.save(appointmentService);
        }
        
        // Tính lại tổng tiền
        calculateTotalAmount(savedAppointment.getId());
        
        return savedAppointment;
    }
    
    @Transactional
    public Appointment updateAppointment(Appointment appointment) {
        if (isAppointmentTimeConflict(appointment)) {
            throw new RuntimeException("Lịch đặt bị trùng với lịch đã tồn tại. Vui lòng chọn thời gian khác.");
        }
        
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }
    
    @Transactional
    public void updateAppointmentServices(Long appointmentId, List<Long> serviceIds) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        // Xóa các dịch vụ hiện tại
        List<com.skincare.model.AppointmentService> existingServices = appointmentServiceRepository.findByAppointmentId(appointmentId);
        appointmentServiceRepository.deleteAll(existingServices);
        
        // Thêm các dịch vụ mới
        for (Long serviceId : serviceIds) {
            com.skincare.model.Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại: " + serviceId));
            
            com.skincare.model.AppointmentService appointmentService = new com.skincare.model.AppointmentService();
            appointmentService.setAppointment(appointment);
            appointmentService.setService(service);
            appointmentService.setPrice(service.getPrice());
            appointmentService.setStatus(ServiceStatus.PENDING);
            
            appointmentServiceRepository.save(appointmentService);
        }
        
        // Tính lại tổng tiền
        calculateTotalAmount(appointmentId);
    }
    
    @Transactional
    public void cancelAppointment(Long id, String reason, boolean isCustomerCancellation) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + id));
        
        // Kiểm tra trạng thái hiện tại
        if (appointment.getStatus() == AppointmentStatus.CHECKED_IN || 
            appointment.getStatus() == AppointmentStatus.IN_PROGRESS) {
            throw new RuntimeException("Không thể hủy lịch hẹn đang được thực hiện");
        }
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || 
            appointment.getStatus() == AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Không thể hủy lịch hẹn đã hoàn thành");
        }
        
        // Áp dụng chính sách hủy lịch
        if (isCustomerCancellation) {
            // Kiểm tra thời gian hủy lịch so với thời gian hẹn
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime appointmentTime = appointment.getAppointmentDate();
            
            // Nếu hủy trước 24 giờ - không phí
            // Nếu hủy trong khoảng 2-24 giờ - phí 30%
            // Nếu hủy dưới 2 giờ - phí 50%
            if (now.plusHours(24).isBefore(appointmentTime)) {
                appointment.setCancellationFee(BigDecimal.ZERO);
            } else if (now.plusHours(2).isBefore(appointmentTime)) {
                BigDecimal fee = appointment.getTotalAmount().multiply(new BigDecimal("0.3"));
                appointment.setCancellationFee(fee);
            } else {
                BigDecimal fee = appointment.getTotalAmount().multiply(new BigDecimal("0.5"));
                appointment.setCancellationFee(fee);
            }
        }
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointment.setCancelledAt(LocalDateTime.now());
        
        appointmentRepository.save(appointment);
        
        // Cập nhật trạng thái các dịch vụ
        List<com.skincare.model.AppointmentService> services = appointmentServiceRepository.findByAppointmentId(id);
        for (com.skincare.model.AppointmentService service : services) {
            service.setStatus(ServiceStatus.CANCELLED);
            appointmentServiceRepository.save(service);
        }
    }
    
    @Transactional
    public void confirmAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + id));
        
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể xác nhận lịch hẹn đang ở trạng thái chờ xác nhận");
        }
        
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setConfirmedAt(LocalDateTime.now());
        
        appointmentRepository.save(appointment);
    }
    
    @Transactional
    public void checkInAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + id));
        
        // Kiểm tra trạng thái hiện tại
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED && 
            appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể check-in cho lịch hẹn đã xác nhận hoặc đang chờ");
        }
        
        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointment.setCheckinTime(LocalDateTime.now());
        
        appointmentRepository.save(appointment);
    }
    
    @Transactional
    public void assignTherapist(Long id, Long therapistId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + id));
        
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Chuyên viên không tồn tại: " + therapistId));
        
        // Kiểm tra xem người dùng có phải là chuyên viên không
        boolean isTherapist = therapist.getUserType() == UserType.THERAPIST;
        
        if (!isTherapist) {
            throw new RuntimeException("Người dùng được chỉ định không phải là chuyên viên");
        }
        
        appointment.setTherapist(therapist);
        appointmentRepository.save(appointment);
    }
    
    @Transactional
    public void startService(Long appointmentId, Long serviceId, Long therapistId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        // Kiểm tra trạng thái lịch hẹn
        if (appointment.getStatus() != AppointmentStatus.CHECKED_IN && 
            appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new RuntimeException("Khách hàng chưa check-in");
        }
        
        // Tìm dịch vụ cần thực hiện
        com.skincare.model.AppointmentService appointmentService = appointmentServiceRepository
                .findByAppointmentIdAndServiceId(appointmentId, serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không thuộc lịch hẹn này"));
        
        if (appointmentService.getStatus() != ServiceStatus.PENDING) {
            throw new RuntimeException("Dịch vụ đã được thực hiện hoặc đã hủy");
        }
        
        // Chỉ định chuyên viên thực hiện
        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Chuyên viên không tồn tại: " + therapistId));
        
        appointmentService.setPerformedBy(therapist);
        appointmentService.setStatus(ServiceStatus.IN_PROGRESS);
        appointmentService.setStartTime(LocalDateTime.now());
        
        appointmentServiceRepository.save(appointmentService);
        
        // Cập nhật trạng thái lịch hẹn
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void completeService(Long appointmentId, Long serviceId, String treatmentResults) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        // Tìm dịch vụ cần cập nhật
        com.skincare.model.AppointmentService appointmentService = appointmentServiceRepository
                .findByAppointmentIdAndServiceId(appointmentId, serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không thuộc lịch hẹn này"));
        
        if (appointmentService.getStatus() != ServiceStatus.IN_PROGRESS) {
            throw new RuntimeException("Dịch vụ chưa được bắt đầu hoặc đã hoàn thành");
        }
        
        appointmentService.setStatus(ServiceStatus.COMPLETED);
        appointmentService.setEndTime(LocalDateTime.now());
        appointmentService.setTreatmentResults(treatmentResults);
        
        appointmentServiceRepository.save(appointmentService);
        
        // Kiểm tra xem tất cả dịch vụ đã hoàn thành chưa
        boolean allServicesCompleted = true;
        List<com.skincare.model.AppointmentService> services = appointmentServiceRepository.findByAppointmentId(appointmentId);
        
        for (com.skincare.model.AppointmentService service : services) {
            if (service.getStatus() == ServiceStatus.PENDING || 
                service.getStatus() == ServiceStatus.IN_PROGRESS) {
                allServicesCompleted = false;
                break;
            }
        }
        
        // Nếu tất cả dịch vụ đã hoàn thành, cập nhật trạng thái lịch hẹn
        if (allServicesCompleted) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointmentRepository.save(appointment);
        }
    }
    
    @Transactional
    public void checkOutAppointment(Long id, String paymentMethod) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + id));
        
        // Kiểm tra trạng thái hiện tại
        if (appointment.getStatus() != AppointmentStatus.COMPLETED && 
            appointment.getStatus() != AppointmentStatus.IN_PROGRESS) {
            throw new RuntimeException("Lịch hẹn chưa hoàn thành, không thể check-out");
        }
        
        appointment.setStatus(AppointmentStatus.CHECKED_OUT);
        appointment.setCheckoutTime(LocalDateTime.now());
        
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            appointment.setPaymentMethod(paymentMethod);
            appointment.setPaymentStatus("PAID");
        }
        
        appointmentRepository.save(appointment);
    }
    
    @Transactional
    public void addServiceToAppointment(Long appointmentId, Long serviceId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        com.skincare.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại: " + serviceId));
        
        // Kiểm tra xem dịch vụ đã tồn tại trong lịch hẹn chưa
        Optional<com.skincare.model.AppointmentService> existingService = appointmentServiceRepository
                .findByAppointmentIdAndServiceId(appointmentId, serviceId);
        
        if (existingService.isPresent()) {
            throw new RuntimeException("Dịch vụ này đã có trong lịch hẹn");
        }
        
        // Thêm dịch vụ mới
        com.skincare.model.AppointmentService appointmentService = new com.skincare.model.AppointmentService();
        appointmentService.setAppointment(appointment);
        appointmentService.setService(service);
        appointmentService.setPrice(service.getPrice());
        appointmentService.setStatus(ServiceStatus.PENDING);
        
        appointmentServiceRepository.save(appointmentService);
        
        // Tính lại tổng tiền
        calculateTotalAmount(appointmentId);
    }
    
    @Transactional
    public void removeServiceFromAppointment(Long appointmentId, Long serviceId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
        
        // Kiểm tra trạng thái lịch hẹn
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || 
            appointment.getStatus() == AppointmentStatus.CHECKED_OUT) {
            throw new RuntimeException("Không thể xóa dịch vụ từ lịch hẹn đã hoàn thành");
        }
        
        // Tìm dịch vụ cần xóa
        com.skincare.model.AppointmentService appointmentService = appointmentServiceRepository
                .findByAppointmentIdAndServiceId(appointmentId, serviceId)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không thuộc lịch hẹn này"));
        
        // Kiểm tra trạng thái dịch vụ
        if (appointmentService.getStatus() == ServiceStatus.IN_PROGRESS || 
            appointmentService.getStatus() == ServiceStatus.COMPLETED) {
            throw new RuntimeException("Không thể xóa dịch vụ đang thực hiện hoặc đã hoàn thành");
        }
        
        // Xóa dịch vụ
        appointmentServiceRepository.delete(appointmentService);
        
        // Tính lại tổng tiền
        calculateTotalAmount(appointmentId);
    }
    
    private int calculateTotalDuration(List<Long> serviceIds) {
        return serviceIds.stream()
            .map(id -> serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại: " + id)))
            .mapToInt(service -> service.getDuration())
            .sum();
    }
    
    /**
     * Kiểm tra xem lịch đặt có bị trùng với lịch đặt khác không
     * Một lịch đặt được coi là trùng khi:
     * 1. Cùng chuyên viên trị liệu và thời gian bị chồng chéo
     * 2. Cùng khách hàng và thời gian bị chồng chéo
     */
    private boolean isAppointmentTimeConflict(Appointment appointment) {
        LocalDateTime startTime = appointment.getAppointmentDate();
        // Mặc định mỗi lịch hẹn kéo dài 1 giờ
        LocalDateTime endTime = startTime.plusHours(1);
        
        List<Appointment> conflictingAppointments = appointmentRepository
            .findByTherapistAndAppointmentDateBetweenAndStatusNotIn(
                appointment.getTherapist(),
                startTime,
                endTime,
                Arrays.asList(AppointmentStatus.CANCELLED, AppointmentStatus.NO_SHOW)
            );
            
        return !conflictingAppointments.isEmpty() && 
               (appointment.getId() == null || 
                conflictingAppointments.stream()
                    .anyMatch(a -> !a.getId().equals(appointment.getId())));
    }

    @Transactional
    public void calculateTotalAmount(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại: " + appointmentId));
            
        BigDecimal total = appointmentServiceRepository.findByAppointmentId(appointmentId)
            .stream()
            .map(as -> as.getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        appointment.setTotalAmount(total);
        appointmentRepository.save(appointment);
    }
    
    /**
     * Lấy danh sách lịch hẹn gần đây, giới hạn số lượng
     */
    public List<Appointment> getRecentAppointments(int limit) {
        return appointmentRepository.findRecentAppointments(limit);
    }

    private void checkPermission(User user, UserType... allowedTypes) {
        if (user == null || user.getUserType() == null) {
            throw new RuntimeException("Người dùng không có quyền thực hiện thao tác này");
        }
        
        boolean hasPermission = Arrays.stream(allowedTypes)
            .anyMatch(type -> type == user.getUserType());
            
        if (!hasPermission) {
            throw new RuntimeException("Người dùng không có quyền thực hiện thao tác này");
        }
    }
} 