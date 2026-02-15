package com.example.sms.controller;

import com.example.sms.entity.Teacher;
import com.example.sms.entity.User;
import com.example.sms.service.TeacherService;
import com.example.sms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TeacherService teacherService;

    @InjectMocks
    private AuthController authController;

    private User user;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("teacher1");
        user.setPassword("encodedPassword");
        user.setRole(User.Role.ROLE_TEACHER);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Dr. Smith");
        teacher.setUser(user);
    }

    @Test
    @DisplayName("Test 4: Register teacher successfully")
    void testRegisterTeacher() {
        // Given
        when(userService.createUser("teacher1", "password123", User.Role.ROLE_TEACHER))
                .thenReturn(user);
        when(teacherService.saveTeacher(any(Teacher.class))).thenReturn(teacher);

        // When
        Teacher result = authController.registerTeacher("teacher1", "password123", "Dr. Smith");

        // Then
        assertNotNull(result);
        assertEquals("Dr. Smith", result.getName());
        assertEquals(user, result.getUser());
        verify(userService, times(1)).createUser("teacher1", "password123", User.Role.ROLE_TEACHER);
        verify(teacherService, times(1)).saveTeacher(any(Teacher.class));
    }
}
