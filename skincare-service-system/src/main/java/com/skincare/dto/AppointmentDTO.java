package com.skincare.dto;

import com.skincare.model.Service;
import lombok.Data;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class AppointmentDTO {
    
    private Long id;
    
    @NotEmpty(message = "Vui lòng chọn ít nhất một dịch vụ")
    private Set<Service> services;
    
    private Long specialistId;
    
    @NotNull(message = "Vui lòng chọn ngày giờ")
    @Future(message = "Ngày giờ phải trong tương lai")
    private LocalDateTime appointmentDateTime;
    
    private String customerName;
    
    @NotNull(message = "Vui lòng nhập số điện thoại")
    private String phoneNumber;
    
    @NotNull(message = "Vui lòng nhập email")
    private String email;
    
    private String notes;
} 