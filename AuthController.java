package com.apihub.controller;

/**
 * Điều khiển xác thực người dùng
 * Lưu ý: Class này được chuyển thành Java thuần tạm thời
 * Cần cài đặt Maven và chạy 'mvn install' để sử dụng Spring Boot
 */
public class AuthController {

    /**
     * Hiển thị trang đăng nhập
     */
    public String showLoginPage() {
        return "login";
    }

    /**
     * Hiển thị trang đăng ký
     */
    public String showRegisterPage() {
        return "register";
    }

    /**
     * Hiển thị trang quên mật khẩu
     */
    public String showForgotPasswordPage() {
        return "forgot-password";
    }
} 