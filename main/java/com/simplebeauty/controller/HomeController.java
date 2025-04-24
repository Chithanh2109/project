package com.simplebeauty.controller;

import com.simplebeauty.model.Service;
import com.simplebeauty.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ServiceService serviceService;

    @Autowired
    public HomeController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Service> featuredServices = serviceService.getAllServices();
        model.addAttribute("featuredServices", featuredServices);
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
