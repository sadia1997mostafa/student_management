package com.example.sms.controller;

import com.example.sms.entity.Teacher;
import com.example.sms.entity.User;
import com.example.sms.service.TeacherService;
import com.example.sms.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final TeacherService teacherService;

    public AuthController(UserService userService,
                          TeacherService teacherService) {
        this.userService = userService;
        this.teacherService = teacherService;
    }

    @PostMapping("/register-teacher")
    public Teacher registerTeacher(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String name) {

        User user = userService.createUser(
                username,
                password,
                User.Role.ROLE_TEACHER
        );

        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setUser(user);

        return teacherService.saveTeacher(teacher);
    }
}
