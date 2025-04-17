package com.skincare.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.skincare.model.SkinConcern;
import com.skincare.repository.SkinConcernRepository;

@Configuration
public class SkinConcernDataInitializer {

    @Bean
    public CommandLineRunner initSkinConcerns(SkinConcernRepository skinConcernRepository) {
        return args -> {
            // Chỉ thêm dữ liệu nếu bảng rỗng
            if (skinConcernRepository.count() == 0) {
                // Dữ liệu tương ứng với enum cũ
                skinConcernRepository.save(createSkinConcern("ACNE", "Mụn"));
                skinConcernRepository.save(createSkinConcern("AGING", "Lão hóa"));
                skinConcernRepository.save(createSkinConcern("DARK_SPOTS", "Đốm nâu"));
                skinConcernRepository.save(createSkinConcern("DRYNESS", "Khô da"));
                skinConcernRepository.save(createSkinConcern("HYPERPIGMENTATION", "Tăng sắc tố"));
                skinConcernRepository.save(createSkinConcern("LARGE_PORES", "Lỗ chân lông to"));
                skinConcernRepository.save(createSkinConcern("REDNESS", "Đỏ da"));
                skinConcernRepository.save(createSkinConcern("SENSITIVITY", "Nhạy cảm"));
                skinConcernRepository.save(createSkinConcern("WRINKLES", "Nếp nhăn"));
            }
        };
    }
    
    private SkinConcern createSkinConcern(String name, String description) {
        SkinConcern concern = new SkinConcern();
        concern.setName(name);
        concern.setDescription(description);
        return concern;
    }
} 