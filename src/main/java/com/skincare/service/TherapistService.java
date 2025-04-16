package com.skincare.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skincare.model.User;
import com.skincare.model.UserType;
import com.skincare.repository.UserRepository;

@Service
public class TherapistService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllTherapists() {
        return userRepository.findByUserType(UserType.THERAPIST);
    }

    public Optional<User> getTherapistById(Long id) {
        return userRepository.findById(id);
    }

    public User createTherapist(User therapist) {
        therapist.setUserType(UserType.THERAPIST);
        return userRepository.save(therapist);
    }

    public User updateTherapist(Long id, User therapistDetails) {
        User therapist = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với id: " + id));
        
        therapist.setFullName(therapistDetails.getFullName());
        therapist.setEmail(therapistDetails.getEmail());
        therapist.setPhoneNumber(therapistDetails.getPhoneNumber());
        therapist.setAddress(therapistDetails.getAddress());
        
        return userRepository.save(therapist);
    }

    public void deleteTherapist(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * Get featured therapists for homepage display
     * @return List of featured therapists
     */
    public List<User> getFeaturedTherapists() {
        // For now, we'll return all active therapists as featured
        // This could be enhanced to filter by a "featured" flag in the future
        return userRepository.findActiveTherapists();
    }
} 