package com.skincare.service;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentService;
import com.skincare.model.Service;
import com.skincare.model.User;
import com.skincare.model.Customer;
import com.skincare.model.AppointmentStatus;
import com.skincare.model.ServiceStatus;
import com.skincare.model.UserType;
import com.skincare.repository.AppointmentRepository;
import com.skincare.repository.AppointmentServiceRepository;
import com.skincare.repository.UserRepository;
import com.skincare.repository.CustomerRepository;
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
public class AppointmentServiceImpl {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final ServiceService serviceService;
    private final UserService userService;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                      UserRepository userRepository,
                                      CustomerRepository customerRepository,
                                      AppointmentServiceRepository appointmentServiceRepository,
                                      ServiceService serviceService,
                                      UserService userService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.serviceService = serviceService;
        this.userService = userService;
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
                LocalDateTime.now(), List.of(AppointmentStatus.CANCELLED));
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
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Lưu các dịch vụ cho lịch hẹn
        for (Long serviceId : serviceIds) {
            Service service = serviceService.getServiceById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại: " + serviceId));
            
            AppointmentService appointmentService = new AppointmentService();
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
    
    private int calculateTotalDuration(List<Long> serviceIds) {
        return serviceIds.stream()
                .map(serviceId -> serviceService.getServiceById(serviceId)
                        .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại: " + serviceId)))
                .mapToInt(Service::getDuration)
                .sum();
    }
    
    /**
     * Kiểm tra xem lịch đặt có bị trùng với lịch đặt khác không
     * Một lịch đặt được coi là trùng khi:
     * 1. Cùng chuyên viên trị liệu và thời gian bị chồng chéo
     * 2. Cùng khách hàng và thời gian bị chồng chéo
     */
    private boolean isAppointmentTimeConflict(Appointment appointment) {
        LocalDateTime appointmentStart = appointment.getAppointmentDate();
        LocalDateTime appointmentEnd = appointmentStart.plusMinutes(calculateTotalDuration(appointment.getServices().stream()
                .map(as -> as.getService().getId())
                .collect(Collectors.toList())));

        List<Appointment> existingAppointments;
        
        if (appointment.getCustomer() != null) {
            existingAppointments = appointmentRepository.findByCustomerAndStatusNotInAndAppointmentDateBetween(
                    appointment.getCustomer(), 
                    List.of(AppointmentStatus.CANCELLED),
                    appointmentStart.minusHours(1),
                    appointmentEnd.plusHours(1));
        } else {
            existingAppointments = appointmentRepository.findByTherapistAndStatusNotInAndAppointmentDateBetween(
                    appointment.getTherapist(), 
                    List.of(AppointmentStatus.CANCELLED),
                    appointmentStart.minusHours(1),
                    appointmentEnd.plusHours(1));
        }

        return !existingAppointments.isEmpty();
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn: " + id));

        if (user.getUserType() != UserType.ADMIN && 
            user.getUserType() != UserType.THERAPIST && 
            !appointment.getCustomer().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy lịch hẹn này");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        return true;
    }

    @Transactional
    public Appointment checkIn(Long id, User staff) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn: " + id));

        if (staff.getUserType() != UserType.ADMIN && staff.getUserType() != UserType.THERAPIST) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này");
        }

        appointment.setStatus(AppointmentStatus.CHECKED_IN);
        appointment.setCheckinTime(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment assignTherapist(Long appointmentId, Long therapistId, User staff) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn: " + appointmentId));

        User therapist = userRepository.findById(therapistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên: " + therapistId));

        if (therapist.getUserType() != UserType.THERAPIST) {
            throw new RuntimeException("Người dùng không phải là chuyên viên");
        }

        appointment.setTherapist(therapist);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment startService(Long appointmentId, Long serviceId, User therapist) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn: " + appointmentId));

        if (therapist.getUserType() != UserType.THERAPIST) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này");
        }

        AppointmentService appointmentService = appointment.getServices().stream()
                .filter(as -> as.getService().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ trong lịch hẹn"));

        appointmentService.setStatus(ServiceStatus.IN_PROGRESS);
        appointmentService.setStartTime(LocalDateTime.now());
        appointmentService.setPerformedBy(therapist);
        appointmentServiceRepository.save(appointmentService);

        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment completeService(Long appointmentId, Long serviceId, String notes, User therapist) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn: " + appointmentId));

        if (therapist.getUserType() != UserType.THERAPIST) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này");
        }

        AppointmentService appointmentService = appointment.getServices().stream()
                .filter(as -> as.getService().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ trong lịch hẹn"));

        appointmentService.setStatus(ServiceStatus.COMPLETED);
        appointmentService.setEndTime(LocalDateTime.now());
        appointmentService.setTreatmentResults(notes);
        appointmentServiceRepository.save(appointmentService);

        boolean allCompleted = appointment.getServices().stream()
                .allMatch(as -> as.getStatus() == ServiceStatus.COMPLETED);

        if (allCompleted) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
        }

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment checkOut(Long id, User staff) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn: " + id));

        if (staff.getUserType() != UserType.ADMIN && staff.getUserType() != UserType.THERAPIST) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setCheckoutTime(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }
} 