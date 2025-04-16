package com.skincare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.skincare")
@EntityScan("com.skincare.model")
@EnableJpaRepositories("com.skincare.repository")
@EnableJpaAuditing
public class SkincareServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkincareServiceApplication.class, args);
    }
} 