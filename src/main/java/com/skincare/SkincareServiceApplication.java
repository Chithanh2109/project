package com.skincare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.skincare")
@EnableJpaAuditing
public class SkincareServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkincareServiceApplication.class, args);
    }
} 