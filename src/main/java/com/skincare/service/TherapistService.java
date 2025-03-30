package com.skincare.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.skincare.model.Service;
import com.skincare.model.User;
import com.skincare.model.User.UserType;
import com.skincare.repository.UserRepository;

@org.springframework.stereotype.Service
public class TherapistService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public TherapistService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    private List<User> therapists = new ArrayList<>();
    
    // Phương thức lấy danh sách chuyên viên trị liệu đang hoạt động
    public List<User> getAllActiveTherapists() {
        return therapists.stream()
                .filter(user -> UserType.THERAPIST.equals(user.getUserType()))
                .filter(User::isEnabled)
                .collect(Collectors.toList());
    }
    
    // Phương thức lấy thông tin chi tiết của một chuyên viên trị liệu
    public Optional<User> getTherapistById(Long id) {
        return therapists.stream()
                .filter(user -> user.getId().equals(id) && UserType.THERAPIST.equals(user.getUserType()))
                .findFirst();
    }
    
    // Phương thức lấy danh sách chuyên viên trị liệu nổi bật
    public List<User> getFeaturedTherapists() {
        return therapists.stream()
                .filter(user -> UserType.THERAPIST.equals(user.getUserType()))
                .filter(User::isEnabled)
                .limit(3)
                .collect(Collectors.toList());
    }
    
    // Phương thức lấy danh sách chuyên môn của chuyên viên trị liệu
    public List<Service> getTherapistSpecialties(Long therapistId) {
        // Trong thực tế, có thể lấy từ một bảng trung gian lưu chuyên môn của chuyên viên
        return new ArrayList<>();
    }
    
    // Phương thức tạo hoặc cập nhật chuyên viên trị liệu
    public User saveTherapist(User therapist) {
        therapist.setUserType(UserType.THERAPIST);
        if (therapist.getId() == null) {
            Long newId = therapists.stream()
                    .mapToLong(User::getId)
                    .max()
                    .orElse(0L) + 1;
            therapist.setId(newId);
            therapists.add(therapist);
        } else {
            for (int i = 0; i < therapists.size(); i++) {
                if (therapists.get(i).getId().equals(therapist.getId())) {
                    therapists.set(i, therapist);
                    break;
                }
            }
        }
        return therapist;
    }
    
    // Phương thức xóa chuyên viên trị liệu
    public void deleteTherapist(Long id) {
        therapists.removeIf(user -> user.getId().equals(id));
    }
} 