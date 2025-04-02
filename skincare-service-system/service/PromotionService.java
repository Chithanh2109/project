package com.skincare.service;

import com.skincare.model.Promotion;
import com.skincare.model.PromotionType;
import com.skincare.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final EmailService emailService;

    @Transactional
    public Promotion createPromotion(String code, String description, PromotionType type,
                                   double value, LocalDateTime startDate, LocalDateTime endDate,
                                   int maxUses, double minOrderAmount) {
        if (promotionRepository.existsByCode(code)) {
            throw new RuntimeException("Mã khuyến mãi đã tồn tại");
        }

        Promotion promotion = new Promotion();
        promotion.setCode(code);
        promotion.setDescription(description);
        promotion.setType(type);
        promotion.setValue(value);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setMaxUses(maxUses);
        promotion.setCurrentUses(0);
        promotion.setMinOrderAmount(minOrderAmount);
        promotion.setActive(true);
        promotion.setCreatedAt(LocalDateTime.now());

        return promotionRepository.save(promotion);
    }

    @Transactional
    public Promotion updatePromotion(Long promotionId, String description, LocalDateTime startDate,
                                   LocalDateTime endDate, int maxUses, double minOrderAmount) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));

        promotion.setDescription(description);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setMaxUses(maxUses);
        promotion.setMinOrderAmount(minOrderAmount);
        promotion.setUpdatedAt(LocalDateTime.now());

        return promotionRepository.save(promotion);
    }

    @Transactional
    public void deactivatePromotion(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));

        promotion.setActive(false);
        promotion.setUpdatedAt(LocalDateTime.now());

        promotionRepository.save(promotion);
    }

    @Transactional
    public void activatePromotion(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));

        promotion.setActive(true);
        promotion.setUpdatedAt(LocalDateTime.now());

        promotionRepository.save(promotion);
    }

    public Optional<Promotion> getPromotionByCode(String code) {
        return promotionRepository.findByCode(code);
    }

    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByActiveTrueAndEndDateAfterOrderByStartDateDesc(LocalDateTime.now());
    }

    public List<Promotion> getExpiredPromotions() {
        return promotionRepository.findByEndDateBeforeOrderByEndDateDesc(LocalDateTime.now());
    }

    public List<Promotion> getUpcomingPromotions() {
        return promotionRepository.findByStartDateAfterOrderByStartDateAsc(LocalDateTime.now());
    }

    public double calculateDiscount(Promotion promotion, double orderAmount) {
        if (!isPromotionValid(promotion, orderAmount)) {
            return 0;
        }

        if (promotion.getType() == PromotionType.PERCENTAGE) {
            return orderAmount * (promotion.getValue() / 100);
        } else {
            return promotion.getValue();
        }
    }

    public boolean isPromotionValid(Promotion promotion, double orderAmount) {
        LocalDateTime now = LocalDateTime.now();
        return promotion.isActive() &&
                now.isAfter(promotion.getStartDate()) &&
                now.isBefore(promotion.getEndDate()) &&
                promotion.getCurrentUses() < promotion.getMaxUses() &&
                orderAmount >= promotion.getMinOrderAmount();
    }

    @Transactional
    public void incrementPromotionUses(Promotion promotion) {
        promotion.setCurrentUses(promotion.getCurrentUses() + 1);
        promotionRepository.save(promotion);

        // Gửi email thông báo khi khuyến mãi sắp hết lượt sử dụng
        if (promotion.getCurrentUses() >= promotion.getMaxUses() * 0.8) {
            emailService.sendPromotionUsageAlert(promotion);
        }
    }

    public Map<String, Object> getPromotionStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        List<Promotion> promotions = promotionRepository.findByCreatedAtBetween(startDate, endDate);

        // Thống kê theo loại khuyến mãi
        Map<PromotionType, Long> typeCount = new HashMap<>();
        for (PromotionType type : PromotionType.values()) {
            typeCount.put(type, promotions.stream()
                    .filter(p -> p.getType() == type)
                    .count());
        }

        // Thống kê tổng lượt sử dụng
        long totalUses = promotions.stream()
                .mapToLong(Promotion::getCurrentUses)
                .sum();

        // Thống kê tổng giá trị khuyến mãi
        double totalValue = promotions.stream()
                .mapToDouble(Promotion::getValue)
                .sum();

        statistics.put("totalPromotions", promotions.size());
        statistics.put("typeCount", typeCount);
        statistics.put("totalUses", totalUses);
        statistics.put("totalValue", totalValue);

        return statistics;
    }
} 