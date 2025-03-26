package com.apihub.config;

/**
 * Cấu hình bảo mật cho API Hub
 * Lưu ý: Class này được chuyển thành Java thuần tạm thời
 * Cần cài đặt Maven và chạy 'mvn install' để sử dụng Spring Security
 */
public class SecurityConfig {

    /**
     * Tạo password encoder để mã hóa mật khẩu
     */
    public Object passwordEncoder() {
        // BCryptPasswordEncoder sẽ được sử dụng sau khi cài đặt Spring Security
        return null;
    }

    /**
     * Cấu hình xác thực người dùng
     */
    protected void configureAuthentication(Object auth) throws Exception {
        // Sẽ được cài đặt sau khi có Spring Security
    }

    /**
     * Cấu hình bảo mật HTTP
     * - Cho phép truy cập trang chủ, dịch vụ, tài nguyên tĩnh
     * - Yêu cầu quyền ADMIN cho các trang quản trị
     * - Xác thực cho các yêu cầu khác
     * - Sử dụng trang đăng nhập tùy chỉnh
     * - Cho phép đăng xuất
     */
    protected void configureHttp(Object http) throws Exception {
        // Cấu hình quyền truy cập sẽ được triển khai sau khi cài đặt Spring Security
    }
} 