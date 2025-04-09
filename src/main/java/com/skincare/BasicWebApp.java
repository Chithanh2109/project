package com.skincare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
    org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@ComponentScan(
    useDefaultFilters = false,
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = BasicWebApp.class
    )
)
@RestController
public class BasicWebApp {

    public static void main(String[] args) {
        SpringApplication.run(BasicWebApp.class, args);
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