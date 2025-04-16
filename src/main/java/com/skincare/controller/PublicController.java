package com.skincare.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skincare.model.User;
import com.skincare.model.UserType;
import com.skincare.service.ServiceService;
import com.skincare.service.TherapistService;
import com.skincare.service.UserService;

import jakarta.validation.Valid;

/**
 * Controller handling public pages for the website visitors
 */
@Controller
public class PublicController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private TherapistService therapistService;
    
    /**
     * Home page
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredTherapists", therapistService.getFeaturedTherapists());
        model.addAttribute("popularServices", serviceService.getPopularServices());
        return "index";
    }
    
    /**
     * Login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * Registration page
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    /**
     * Process registration
     */
    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("user") User user, 
                                BindingResult bindingResult, 
                                Model model, 
                                RedirectAttributes redirectAttributes) {
        
        // Check if username already exists
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.user", "Tên đăng nhập đã tồn tại");
        }
        
        // Check if email already exists
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", "Email đã được sử dụng");
        }
        
        // Check if password and confirm password match
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        // Set default role as CUSTOMER
        user.setUserType(UserType.CUSTOMER);
        user.setIsActive(true);
        
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Đăng ký tài khoản thành công! Vui lòng đăng nhập để tiếp tục.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "register";
        }
    }
    
    /**
     * Display all therapists
     */
    @GetMapping("/therapists")
    public String therapists(Model model) {
        List<User> therapists = userService.getUsersByType(UserType.THERAPIST);
        model.addAttribute("therapists", therapists);
        return "therapists";
    }
    
    /**
     * Display therapist details
     */
    @GetMapping("/therapists/{id}")
    public String therapistDetail(@PathVariable Long id, Model model) {
        Optional<User> therapist = userService.findById(id);
        if (therapist.isPresent()) {
            model.addAttribute("therapist", therapist.get());
            return "therapist-detail";
        }
        return "redirect:/therapists";
    }
    
    /**
     * Display all services
     */
    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", serviceService.getAllServices());
        model.addAttribute("categories", serviceService.getAllCategories());
        return "services";
    }
    
    /**
     * Display service details
     */
    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        return serviceService.getServiceById(id)
                .map(service -> {
                    model.addAttribute("service", service);
                    model.addAttribute("relatedServices", serviceService.getRelatedServices(id));
                    return "service-detail";
                })
                .orElse("redirect:/services");
    }
    
    /**
     * About page
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    /**
     * Contact page
     */
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
} 