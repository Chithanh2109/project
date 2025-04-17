package com.skincare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleTestController {

    @GetMapping("/simple")
    public String simple() {
        return "Simple test successful";
    }

    @GetMapping("/simpleping")
    public String ping() {
        return "pong";
    }
    
    @GetMapping("/helloworld")
    public String helloWorld() {
        return "Hello World! Ứng dụng Spring Boot đang hoạt động.";
    }
    
    @GetMapping("/test-connection")
    public String testConnection() {
        return "Kết nối thành công! Máy chủ đang chạy.";
    }
} 