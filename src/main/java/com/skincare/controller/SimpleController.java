package com.skincare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello World! The application is running.";
    }
    
    @GetMapping("/api/status")
    public String status() {
        return "Application is running with H2 database.";
    }
} 