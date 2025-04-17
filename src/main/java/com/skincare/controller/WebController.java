package com.skincare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    @GetMapping("/blog")
    public String blog() {
        return "blog";
    }
    
    @GetMapping("/services")
    public String services() {
        return "services";
    }
} 