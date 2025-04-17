package com.skincare;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkincareServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkincareServiceApplication.class, args);
        
        // Lấy địa chỉ IP máy tính
        String ipAddress = "127.0.0.1"; // Mặc định
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // Giữ địa chỉ mặc định nếu có lỗi
        }
        
        System.out.println("==========================================================");
        System.out.println("    SKINCARE SERVICE STARTED SUCCESSFULLY!");
        System.out.println("    Access it via IP: http://" + ipAddress + ":8080");
        System.out.println("    Simple test: http://" + ipAddress + ":8080/simple");
        System.out.println("==========================================================");
    }
} 