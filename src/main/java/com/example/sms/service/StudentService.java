package com.example.sms.service;

import com.example.sms.entity.Student;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    public Student updateAllowed(Student existing, Student updated) {
        existing.setName(updated.getName());
        existing.setDepartment(updated.getDepartment());
        existing.setCourses(updated.getCourses());
        return existing;
    }
}
