package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebOnlyApp {
    
    public static void main(String[] args) {
        SpringApplication.run(WebOnlyApp.class, args);
    }
    
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.setPort(8080);
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