package com.example.sms.controller;

import com.example.sms.entity.Student;
import com.example.sms.repository.StudentRepository;
import com.example.sms.service.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public StudentController(StudentRepository studentRepository,
                             StudentService studentService) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    @PutMapping("/update/{id}")
    public Student updateStudent(@PathVariable Long id,
                                 @RequestBody Student updatedStudent) {

        Student existing = studentRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Ensure the authenticated user matches the student being updated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (existing.getUser() == null || !existing.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own profile");
        }

        // Let the service copy only allowed fields (service already enforces allowed fields)
        studentService.updateAllowed(existing, updatedStudent);
        return studentRepository.save(existing);
    }
}
