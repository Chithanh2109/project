package com.skincare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@RestController
public class SimpleWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleWebApplication.class, args);
    }
    
    @GetMapping("/")
    public String home() {
        return "Welcome to Skincare Service!";
    }
    
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
} 