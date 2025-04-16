package com.skincare.service;

import java.util.List;
import java.util.Optional;

import com.skincare.model.Category;
import com.skincare.model.Quiz;
import com.skincare.model.Service;

public interface ServiceService {
    List<Service> getAllServices();
    List<Service> getActiveServices();
    Optional<Service> getServiceById(Long id);
    List<Service> getServicesByCategory(Long categoryId);
    List<Service> searchServices(String keyword);
    Service saveService(Service service);
    void deleteService(Long id);
    List<Category> getAllCategories();
    List<Category> getActiveCategories();
    List<Service> getFeaturedServices();
    List<Service> getAllActiveServices();
    List<Service> getRelatedServices(Long serviceId);
    List<Service> getActiveServicesByCategory(Long categoryId);
    Quiz getActiveQuiz();
    Optional<Category> getCategoryById(Long id);
    Category saveCategory(Category category);
    void deleteCategory(Long id);
    Quiz saveQuiz(Quiz quiz);
    List<Service> getPopularServices();
} 