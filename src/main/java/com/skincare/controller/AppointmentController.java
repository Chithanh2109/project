package com.skincare.controller;

import com.skincare.model.Appointment;
import com.skincare.model.AppointmentService;
import com.skincare.model.AppointmentStatus;
import com.skincare.model.User;
import com.skincare.service.AppointmentManagementService;
import com.skincare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    
    private final AppointmentManagementService appointmentService;
    private final UserService userService;
    
    @Autowired
    public AppointmentController(AppointmentManagementService appointmentService, UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByCustomer(@PathVariable Long customerId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByCustomer(customerId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/therapist/{therapistId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByTherapist(@PathVariable Long therapistId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByTherapist(therapistId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<Appointment>> getUpcomingAppointments() {
        List<Appointment> appointments = appointmentService.getUpcomingAppointments();
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/date")
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        return appointment.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> requestData, Authentication authentication) {
        try {
            Long customerId;
            
            // Kiểm tra xem người dùng đã đăng nhập chưa
            if (authentication != null) {
                User currentUser = userService.findByUsername(authentication.getName());
                
                // Nếu là khách hàng đặt lịch cho chính mình
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
                    customerId = currentUser.getId();
                } else {
                    // Nhân viên đặt lịch cho khách hàng
                    customerId = Long.parseLong(requestData.get("customerId").toString());
                }
            } else {
                // Khách vãng lai đặt lịch - cần thông tin liên hệ
                if (!requestData.containsKey("contactName") || !requestData.containsKey("contactPhone")) {
                    return ResponseEntity.badRequest().body("Vui lòng cung cấp tên và số điện thoại liên hệ");
                }
                
                // Tạo tài khoản tạm thời hoặc lưu thông tin liên hệ
                User guestUser = new User();
                guestUser.setFullName(requestData.get("contactName").toString());
                guestUser.setPhone(requestData.get("contactPhone").toString());
                guestUser.setEmail(requestData.getOrDefault("contactEmail", "").toString());
                
                // Lưu thông tin khách vãng lai
                guestUser = userService.saveUser(guestUser);
                customerId = guestUser.getId();
            }
            
            // Xử lý dữ liệu đặt lịch
            Appointment appointment = new Appointment();
            appointment.setCustomer(userService.findById(customerId).orElseThrow());
            
            // Nếu khách hàng chỉ định chuyên viên
            if (requestData.containsKey("therapistId")) {
                Long therapistId = Long.parseLong(requestData.get("therapistId").toString());
                appointment.setTherapist(userService.findById(therapistId).orElse(null));
            }
            
            // Đặt ngày giờ hẹn
            String appointmentDateStr = requestData.get("appointmentDate").toString();
            String appointmentTimeStr = requestData.get("appointmentTime").toString();
            LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDateStr + "T" + appointmentTimeStr);
            appointment.setAppointmentDate(appointmentDateTime);
            
            // Thêm ghi chú nếu có
            if (requestData.containsKey("notes")) {
                appointment.setNotes(requestData.get("notes").toString());
            }
            
            // Lấy danh sách dịch vụ đã chọn
            @SuppressWarnings("unchecked")
            List<Long> serviceIds = (List<Long>) requestData.get("serviceIds");
            
            // Lưu lịch hẹn và dịch vụ
            Appointment savedAppointment = appointmentService.saveAppointment(appointment, serviceIds);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> requestData) {
        try {
            Optional<Appointment> existingAppointment = appointmentService.getAppointmentById(id);
            
            if (existingAppointment.isPresent()) {
                Appointment appointment = existingAppointment.get();
                
                // Cập nhật thông tin lịch hẹn
                if (requestData.containsKey("appointmentDate") && requestData.containsKey("appointmentTime")) {
                    String appointmentDateStr = requestData.get("appointmentDate").toString();
                    String appointmentTimeStr = requestData.get("appointmentTime").toString();
                    LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDateStr + "T" + appointmentTimeStr);
                    appointment.setAppointmentDate(appointmentDateTime);
                }
                
                // Cập nhật chuyên viên nếu có
                if (requestData.containsKey("therapistId")) {
                    Long therapistId = Long.parseLong(requestData.get("therapistId").toString());
                    appointment.setTherapist(userService.findById(therapistId).orElse(null));
                }
                
                // Cập nhật ghi chú nếu có
                if (requestData.containsKey("notes")) {
                    appointment.setNotes(requestData.get("notes").toString());
                }
                
                // Cập nhật trạng thái nếu có
                if (requestData.containsKey("status")) {
                    AppointmentStatus status = AppointmentStatus.valueOf(requestData.get("status").toString());
                    appointment.setStatus(status);
                }
                
                // Cập nhật dịch vụ nếu có
                if (requestData.containsKey("serviceIds")) {
                    @SuppressWarnings("unchecked")
                    List<Long> serviceIds = (List<Long>) requestData.get("serviceIds");
                    appointmentService.updateAppointmentServices(id, serviceIds);
                }
                
                Appointment updatedAppointment = appointmentService.updateAppointment(appointment);
                return ResponseEntity.ok(updatedAppointment);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        try {
            // Xác định người hủy (khách hàng hay nhân viên)
            boolean isCustomer = authentication != null && 
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
            
            appointmentService.cancelAppointment(id, reason, isCustomer);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        try {
            appointmentService.confirmAppointment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/checkin")
    public ResponseEntity<?> checkInAppointment(@PathVariable Long id) {
        try {
            appointmentService.checkInAppointment(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/start-service/{serviceId}")
    public ResponseEntity<?> startService(
            @PathVariable Long id, 
            @PathVariable Long serviceId, 
            @RequestBody Map<String, Object> requestData,
            Authentication authentication) {
        try {
            // Lấy ID chuyên viên thực hiện dịch vụ
            Long therapistId = null;
            if (authentication != null) {
                User currentUser = userService.findByUsername(authentication.getName());
                if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_THERAPIST"))) {
                    therapistId = currentUser.getId();
                } else if (requestData.containsKey("therapistId")) {
                    therapistId = Long.parseLong(requestData.get("therapistId").toString());
                }
            } else if (requestData.containsKey("therapistId")) {
                therapistId = Long.parseLong(requestData.get("therapistId").toString());
            }
            
            if (therapistId == null) {
                return ResponseEntity.badRequest().body("Vui lòng chỉ định chuyên viên thực hiện dịch vụ");
            }
            
            appointmentService.startService(id, serviceId, therapistId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/complete-service/{serviceId}")
    public ResponseEntity<?> completeService(
            @PathVariable Long id, 
            @PathVariable Long serviceId, 
            @RequestBody Map<String, Object> requestData) {
        try {
            String treatmentResults = "";
            if (requestData.containsKey("treatmentResults")) {
                treatmentResults = requestData.get("treatmentResults").toString();
            }
            
            appointmentService.completeService(id, serviceId, treatmentResults);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/checkout")
    public ResponseEntity<?> checkOutAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> requestData) {
        try {
            String paymentMethod = null;
            if (requestData != null && requestData.containsKey("paymentMethod")) {
                paymentMethod = requestData.get("paymentMethod").toString();
            }
            
            appointmentService.checkOutAppointment(id, paymentMethod);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/assign/{therapistId}")
    public ResponseEntity<?> assignTherapist(@PathVariable Long id, @PathVariable Long therapistId) {
        try {
            appointmentService.assignTherapist(id, therapistId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/add-service")
    public ResponseEntity<?> addServiceToAppointment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> requestData) {
        try {
            Long serviceId = Long.parseLong(requestData.get("serviceId").toString());
            appointmentService.addServiceToAppointment(id, serviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}/remove-service/{serviceId}")
    public ResponseEntity<?> removeServiceFromAppointment(
            @PathVariable Long id,
            @PathVariable Long serviceId) {
        try {
            appointmentService.removeServiceFromAppointment(id, serviceId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
} 