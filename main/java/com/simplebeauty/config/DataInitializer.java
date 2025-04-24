package com.simplebeauty.config;

import com.simplebeauty.service.ServiceService;
import com.simplebeauty.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final ServiceService serviceService;

    @Autowired
    public DataInitializer(UserService userService, ServiceService serviceService) {
        this.userService = userService;
        this.serviceService = serviceService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Tạo tài khoản admin nếu chưa tồn tại
        userService.createAdminIfNotExists();
        
        // Tạo dữ liệu mẫu cho dịch vụ
        serviceService.createSampleServices();
        
        System.out.println("Data initialization completed.");
    }
}
