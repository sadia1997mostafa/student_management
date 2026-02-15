package com.example.sms.controller;

import com.example.sms.entity.Student;
import com.example.sms.entity.User;
import com.example.sms.repository.StudentRepository;
import com.example.sms.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private StudentController studentController;

    private Student student;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("student1");
        user.setRole(User.Role.ROLE_STUDENT);

        student = new Student();
        student.setId(1L);
        student.setRollNo("S001");
        student.setName("John Doe");
        student.setUser(user);

        // Mock security context
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Test 1: Update student profile successfully")
    void testUpdateStudentSuccess() {
        // Given
        when(authentication.getName()).thenReturn("student1");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentService.updateAllowed(any(), any())).thenReturn(student);
        when(studentRepository.save(any())).thenReturn(student);

        Student updatedData = new Student();
        updatedData.setName("John Updated");

        // When
        Student result = studentController.updateStudent(1L, updatedData);

        // Then
        assertNotNull(result);
        verify(studentRepository, times(1)).findById(1L);
        verify(studentService, times(1)).updateAllowed(student, updatedData);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    @DisplayName("Test 2: Throw exception when student not found")
    void testUpdateStudentNotFound() {
        // Given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        Student updatedData = new Student();

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            studentController.updateStudent(999L, updatedData);
        });
        verify(studentRepository, times(1)).findById(999L);
        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 3: Throw exception when user tries to update another student's profile")
    void testUpdateStudentUnauthorized() {
        // Given
        when(authentication.getName()).thenReturn("hacker");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student updatedData = new Student();

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            studentController.updateStudent(1L, updatedData);
        });
        verify(studentRepository, never()).save(any());
    }
}
