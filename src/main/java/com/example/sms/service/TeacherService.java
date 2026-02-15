package com.example.sms.service;

import com.example.sms.entity.Student;
import com.example.sms.entity.Teacher;
import com.example.sms.repository.StudentRepository;
import com.example.sms.repository.TeacherRepository;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          StudentRepository studentRepository) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    // Create teacher profile
    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    // Teacher creates a student
    public Student createStudent(Student student, Teacher teacher) {
        student.setTeacher(teacher);   // enforce ownership
        return studentRepository.save(student);
    }
}
