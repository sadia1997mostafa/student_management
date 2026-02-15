package com.example.sms.controller;

import com.example.sms.entity.Course;
import com.example.sms.repository.CourseRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher/courses")
@PreAuthorize("hasRole('TEACHER')")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public Course create(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    @GetMapping
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        courseRepository.deleteById(id);
    }
}
