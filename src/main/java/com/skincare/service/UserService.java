package com.skincare.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skincare.model.Role;
import com.skincare.model.RoleType;
import com.skincare.model.User;
import com.skincare.model.UserType;
import com.skincare.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByType(UserType userType) {
        return userRepository.findByUserType(userType);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Transactional
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean isStaffOrManager(User user) {
        return user != null && (user.getUserType() == UserType.ADMIN || user.getUserType() == UserType.THERAPIST);
    }
    
    public boolean isManager(User user) {
        return user != null && user.getUserType() == UserType.ADMIN;
    }
    
    public boolean isTherapist(User user) {
        return user != null && user.getUserType() == UserType.THERAPIST;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<Role> userRoles = new HashSet<>();
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleType.ROLE_CUSTOMER);
        userRoles.add(userRole);
        user.setRoles(userRoles);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findByFullNameContainingIgnoreCase(keyword);
    }

} 