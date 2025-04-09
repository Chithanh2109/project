package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MinimalApp {

    public static void main(String[] args) {
        SpringApplication.run(MinimalApp.class, args);
    }
    
    @GetMapping("/")
    public String home() {
        return "Welcome to Minimal Web App!";
    }
    
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
} 