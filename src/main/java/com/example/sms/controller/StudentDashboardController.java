package com.example.sms.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentDashboardController {

    @GetMapping("/student/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        // add student-specific model data if needed
        return "student-dashboard";
    }
}
