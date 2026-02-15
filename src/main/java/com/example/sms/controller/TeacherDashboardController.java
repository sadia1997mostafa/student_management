package com.example.sms.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherDashboardController {

    @GetMapping("/teacher/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        // further model attributes (lists of students/courses) can be added here
        return "teacher-dashboard";
    }
}
