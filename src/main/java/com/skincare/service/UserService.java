package com.skincare.service;

import com.skincare.model.User;
import com.skincare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByType(User.UserType userType) {
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
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean isStaffOrManager(User user) {
        return user != null && (user.getUserType() == User.UserType.STAFF || user.getUserType() == User.UserType.MANAGER);
    }
    
    public boolean isManager(User user) {
        return user != null && user.getUserType() == User.UserType.MANAGER;
    }
    
    public boolean isTherapist(User user) {
        return user != null && user.getUserType() == User.UserType.SKIN_THERAPIST;
    }
} 