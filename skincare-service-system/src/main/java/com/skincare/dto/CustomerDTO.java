package com.skincare.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CustomerDTO {
    private Long id;
    
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;
    
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại không hợp lệ")
    private String phone;
    
    private String address;
    private String notes;
    private String avatarUrl;
    private boolean active;
    
    // Thống kê
    private int totalAppointments;
    private double averageRating;
    private double totalSpent;
    private String lastVisit;
} 