package com.skincare.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LanguageController {

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/language")
    public String languageDemo(Model model) {
        Locale locale = LocaleContextHolder.getLocale();
        
        model.addAttribute("welcomeMessage", messageSource.getMessage("app.welcome", null, locale));
        model.addAttribute("appName", messageSource.getMessage("app.name", null, locale));
        model.addAttribute("loginText", messageSource.getMessage("app.login", null, locale));
        model.addAttribute("registerText", messageSource.getMessage("app.register", null, locale));
        
        return "language";
    }

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Model model) {
        Locale locale = LocaleContextHolder.getLocale();
        
        model.addAttribute("name", name);
        model.addAttribute("greeting", messageSource.getMessage("app.welcome", null, locale));
        
        return "greeting";
    }
} 