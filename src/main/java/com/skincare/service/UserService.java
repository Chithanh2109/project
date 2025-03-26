package com.skincare.service;

import com.skincare.model.User;
import com.skincare.model.User.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Service interface cho việc quản lý người dùng
 */
public interface UserService {
    
    /**
     * Lấy tất cả người dùng
     */
    List<User> getAllUsers();
    
    /**
     * Lấy người dùng theo ID
     */
    Optional<User> getUserById(Long id);
    
    /**
     * Lấy người dùng theo username
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * Lấy người dùng theo email
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Lưu hoặc cập nhật thông tin người dùng
     */
    User saveUser(User user);
    
    /**
     * Đăng ký người dùng mới
     */
    User registerUser(User user);
    
    /**
     * Cập nhật thông tin người dùng
     */
    User updateUser(User user);
    
    /**
     * Xóa người dùng theo ID
     */
    void deleteUser(Long id);
    
    /**
     * Kiểm tra username đã tồn tại chưa
     */
    boolean existsByUsername(String username);
    
    /**
     * Kiểm tra email đã tồn tại chưa
     */
    boolean existsByEmail(String email);
    
    /**
     * Lấy danh sách người dùng theo vai trò
     */
    List<User> getUsersByRole(UserRole role);
    
    /**
     * Tìm kiếm người dùng theo tên
     */
    List<User> searchUsersByName(String keyword);
    
    /**
     * Tìm kiếm chuyên viên da
     */
    List<User> findAllTherapists();
    
    /**
     * Kích hoạt hoặc vô hiệu hóa người dùng
     */
    User toggleUserStatus(Long id);
    
    /**
     * Đổi mật khẩu người dùng
     */
    boolean changePassword(Long userId, String currentPassword, String newPassword);
    
    /**
     * Khởi tạo quá trình khôi phục mật khẩu
     */
    boolean initiatePasswordReset(String email);
    
    /**
     * Hoàn tất quá trình khôi phục mật khẩu
     */
    boolean completePasswordReset(String token, String newPassword);
} 