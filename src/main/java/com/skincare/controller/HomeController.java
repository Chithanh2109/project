package com.skincare.controller;

import com.skincare.model.Service;
import com.skincare.model.User;
import com.skincare.service.BlogService;
import com.skincare.service.ServiceService;
import com.skincare.service.TherapistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    
    private final ServiceService serviceService;
    private final TherapistService therapistService;
    private final BlogService blogService;
    
    @Autowired
    public HomeController(ServiceService serviceService, TherapistService therapistService, BlogService blogService) {
        this.serviceService = serviceService;
        this.therapistService = therapistService;
        this.blogService = blogService;
    }
    
    @GetMapping({"/", "/index", "/home"})
    public String home(Model model) {
        model.addAttribute("title", "Skincare Center - Trang chủ");
        model.addAttribute("featuredServices", serviceService.getFeaturedServices());
        model.addAttribute("featuredTherapists", therapistService.getFeaturedTherapists());
        model.addAttribute("recentBlogs", blogService.getRecentBlogs(3));
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Skincare Center - Về chúng tôi");
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("title", "Skincare Center - Liên hệ");
        return "contact";
    }
    
    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("title", "Skincare Center - Dịch vụ");
        model.addAttribute("services", serviceService.getAllActiveServices());
        model.addAttribute("categories", serviceService.getAllCategories());
        return "services";
    }
    
    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        Optional<Service> service = serviceService.getServiceById(id);
        if (service.isPresent()) {
            model.addAttribute("service", service.get());
            model.addAttribute("relatedServices", serviceService.getRelatedServices(id));
            return "service-detail";
        }
        return "redirect:/services";
    }
    
    @GetMapping("/therapists")
    public String allTherapists(Model model) {
        model.addAttribute("therapists", therapistService.getAllActiveTherapists());
        return "therapists";
    }
    
    @GetMapping("/therapists/{id}")
    public String therapistDetail(@PathVariable Long id, Model model) {
        Optional<User> therapist = therapistService.getTherapistById(id);
        if (therapist.isPresent()) {
            model.addAttribute("therapist", therapist.get());
            model.addAttribute("specialties", therapistService.getTherapistSpecialties(id));
            return "therapist-detail";
        }
        return "redirect:/therapists";
    }
    
    @GetMapping("/blog")
    public String blog(Model model) {
        model.addAttribute("blogs", blogService.getAllActiveBlogs());
        return "blog";
    }
    
    @GetMapping("/blog/{id}")
    public String blogDetail(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.getBlogById(id).orElse(null));
        model.addAttribute("recentBlogs", blogService.getRecentBlogs(3));
        return "blog-detail";
    }
    
    @GetMapping("/quiz")
    public String quiz(Model model) {
        model.addAttribute("activeQuiz", serviceService.getActiveQuiz());
        return "quiz";
    }
} 