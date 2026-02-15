package com.example.sms.controller;

import com.example.sms.entity.Teacher;
import com.example.sms.entity.User;
import com.example.sms.service.TeacherService;
import com.example.sms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    private final UserService userService;
    private final TeacherService teacherService;

    public ViewController(UserService userService, TeacherService teacherService) {
        this.userService = userService;
        this.teacherService = teacherService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register-teacher")
    public String showRegisterTeacher() {
        return "register-teacher";
    }

    @PostMapping("/register-teacher")
    public String registerTeacher(@RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam String name) {
        User user = userService.createUser(username, password, User.Role.ROLE_TEACHER);
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setUser(user);
        teacherService.saveTeacher(teacher);
        return "redirect:/login?registered";
    }
}
