package com.example.sms.controller;

import com.example.sms.entity.Student;
import com.example.sms.entity.Teacher;
import com.example.sms.entity.User;
import com.example.sms.repository.TeacherRepository;
import com.example.sms.repository.UserRepository;
import com.example.sms.service.TeacherService;
import com.example.sms.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherRepository teacherRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public TeacherController(TeacherService teacherService,
                             TeacherRepository teacherRepository,
                             UserService userService,
                             UserRepository userRepository) {
        this.teacherService = teacherService;
        this.teacherRepository = teacherRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/create-student-form")
    public String showCreateStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "create-student-form";
    }

    /**
     * Create a student. Optionally create a linked User account by providing username and password.
     * Students cannot self-register; teacher must supply credentials.
     */
    @PostMapping("/create-student")
    public String createStudent(Authentication authentication,
                                @RequestParam(required = false) String username,
                                @RequestParam(required = false) String password,
                                Student student) {

        String currentUsername = authentication.getName();
        User teacherUser = userRepository.findByUsername(currentUsername);
        if (teacherUser == null) {
            throw new RuntimeException("Authenticated user not found");
        }

        Teacher teacher = teacherRepository.findByUser(teacherUser).orElseThrow(() -> new RuntimeException("Teacher profile not found for user"));

        if (username != null && password != null && !username.isEmpty()) {
            // create user with ROLE_STUDENT and attach
            User user = userService.createUser(username, password, User.Role.ROLE_STUDENT);
            student.setUser(user);
        }

        teacherService.createStudent(student, teacher);
        // After creating a student, redirect back to the login page so the teacher can log out / test student login
        return "redirect:/login?studentCreated";
    }
}
