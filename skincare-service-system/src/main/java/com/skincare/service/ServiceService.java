package com.skincare.service;

import com.skincare.entity.Service;
import com.skincare.repository.ServiceRepository;
import com.skincare.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public Optional<Service> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    @Transactional
    public Service createService(Service service) {
        // Validate service data
        validateService(service);
        
        // Set initial values
        service.setActive(true);
        service.setAverageRating(0.0);
        service.setTotalReviews(0L);
        
        return serviceRepository.save(service);
    }

    @Transactional
    public Service updateService(Long id, Service serviceDetails) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service not found"));

        // Validate service data
        validateService(serviceDetails);

        // Update service details
        service.setName(serviceDetails.getName());
        service.setDescription(serviceDetails.getDescription());
        service.setPrice(serviceDetails.getPrice());
        service.setDuration(serviceDetails.getDuration());
        service.setImageUrl(serviceDetails.getImageUrl());
        service.setCategory(serviceDetails.getCategory());

        return serviceRepository.save(service);
    }

    @Transactional
    public void deleteService(Long id) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service not found"));

        // Check if service has any reviews
        if (reviewRepository.countByServiceId(id) > 0) {
            throw new RuntimeException("Cannot delete service with existing reviews");
        }

        serviceRepository.delete(service);
    }

    @Transactional
    public Service toggleServiceStatus(Long id) {
        Service service = serviceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Service not found"));

        service.setActive(!service.isActive());
        return serviceRepository.save(service);
    }

    @Transactional
    public void updateServiceRating(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found"));

        // Calculate new average rating
        Double newRating = reviewRepository.getAverageRatingForService(serviceId);
        Long totalReviews = reviewRepository.countByServiceId(serviceId);

        service.setAverageRating(newRating != null ? newRating : 0.0);
        service.setTotalReviews(totalReviews);

        serviceRepository.save(service);
    }

    private void validateService(Service service) {
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            throw new RuntimeException("Service name is required");
        }
        if (service.getPrice() == null || service.getPrice() <= 0) {
            throw new RuntimeException("Service price must be greater than 0");
        }
        if (service.getDuration() == null || service.getDuration() <= 0) {
            throw new RuntimeException("Service duration must be greater than 0");
        }
        if (service.getCategory() == null) {
            throw new RuntimeException("Service category is required");
        }
    }

    public List<Service> getActiveServices() {
        return serviceRepository.findByActiveTrue();
    }

    public List<Service> getServicesByCategory(String category) {
        return serviceRepository.findByCategory(category);
    }

    public List<Service> searchServices(String keyword) {
        return serviceRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
    }

    public List<Service> getTopRatedServices(int limit) {
        return serviceRepository.findTopRatedServices(limit);
    }

    public List<Service> getMostPopularServices(int limit) {
        return serviceRepository.findMostPopularServices(limit);
    }

    public Map<String, Object> getServiceStatistics(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found"));

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalBookings", serviceRepository.countTotalBookings(serviceId));
        statistics.put("totalRevenue", serviceRepository.calculateTotalRevenue(serviceId));
        statistics.put("averageRating", service.getAverageRating());
        statistics.put("totalReviews", service.getTotalReviews());
        statistics.put("ratingDistribution", reviewRepository.getRatingDistributionForService(serviceId));
        
        return statistics;
    }

    public List<Service> getServicesByPriceRange(double minPrice, double maxPrice) {
        return serviceRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Service> getServicesByDurationRange(int minDuration, int maxDuration) {
        return serviceRepository.findByDurationBetween(minDuration, maxDuration);
    }

    @Transactional
    public void updateServicePrices(List<Long> serviceIds, double percentageChange) {
        List<Service> services = serviceRepository.findAllById(serviceIds);
        for (Service service : services) {
            double newPrice = service.getPrice() * (1 + percentageChange / 100);
            service.setPrice(newPrice);
        }
        serviceRepository.saveAll(services);
    }

    @Transactional
    public void updateServiceDurations(List<Long> serviceIds, int durationChange) {
        List<Service> services = serviceRepository.findAllById(serviceIds);
        for (Service service : services) {
            service.setDuration(service.getDuration() + durationChange);
        }
        serviceRepository.saveAll(services);
    }

    public Map<String, Long> getServiceCategoryDistribution() {
        return serviceRepository.countServicesByCategory();
    }

    public Map<String, Double> getAveragePriceByCategory() {
        return serviceRepository.calculateAveragePriceByCategory();
    }

    public List<Service> getServicesNeedingUpdate(LocalDateTime lastUpdate) {
        return serviceRepository.findServicesNeedingUpdate(lastUpdate);
    }

    @Transactional
    public void archiveService(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        
        service.setActive(false);
        service.setArchived(true);
        service.setArchivedAt(LocalDateTime.now());
        
        serviceRepository.save(service);
    }

    @Transactional
    public void restoreService(Long serviceId) {
        Service service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        
        service.setActive(true);
        service.setArchived(false);
        service.setArchivedAt(null);
        
        serviceRepository.save(service);
    }

    public List<Service> getArchivedServices() {
        return serviceRepository.findByArchivedTrue();
    }
} 