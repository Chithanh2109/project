package com.simplebeauty.service;

import com.simplebeauty.model.Role;
import com.simplebeauty.model.User;
import com.simplebeauty.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);
        
        return userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với id: " + id));
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
    
    public void createAdminIfNotExists() {
        if (!userRepository.existsByEmail("admin@simplebeauty.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@simplebeauty.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEnabled(true);
            
            userRepository.save(admin);
        }
    }
}
