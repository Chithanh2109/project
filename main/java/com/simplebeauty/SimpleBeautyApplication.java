package com.simplebeauty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@Controller
public class SimpleBeautyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleBeautyApplication.class, args);
    }
}