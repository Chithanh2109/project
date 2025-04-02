package com.skincare.service;

import com.skincare.model.Specialist;
import com.skincare.repository.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialistService {

    private final SpecialistRepository specialistRepository;

    @Transactional
    public Specialist createSpecialist(Specialist specialist) {
        if (specialistRepository.existsByEmail(specialist.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        return specialistRepository.save(specialist);
    }

    @Transactional
    public Specialist updateSpecialist(Long id, Specialist specialist) {
        Specialist existingSpecialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));

        existingSpecialist.setFullName(specialist.getFullName());
        existingSpecialist.setPhone(specialist.getPhone());
        existingSpecialist.setSpecialization(specialist.getSpecialization());
        existingSpecialist.setDescription(specialist.getDescription());

        return specialistRepository.save(existingSpecialist);
    }

    @Transactional
    public void deactivateSpecialist(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));
        specialist.setActive(false);
        specialistRepository.save(specialist);
    }

    @Transactional
    public void activateSpecialist(Long id) {
        Specialist specialist = specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));
        specialist.setActive(true);
        specialistRepository.save(specialist);
    }

    public List<Specialist> getAllActiveSpecialists() {
        return specialistRepository.findByActiveTrue();
    }

    public Specialist getSpecialistById(Long id) {
        return specialistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));
    }

    public Specialist getSpecialistByEmail(String email) {
        return specialistRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên viên"));
    }

    public List<Specialist> getSpecialistsBySpecialization(String specialization) {
        return specialistRepository.findBySpecialization(specialization);
    }
} 