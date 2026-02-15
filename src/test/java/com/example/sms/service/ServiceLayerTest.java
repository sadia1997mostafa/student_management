package com.example.sms.service;

import com.example.sms.entity.Student;
import com.example.sms.entity.Teacher;
import com.example.sms.entity.User;
import com.example.sms.entity.Department;
import com.example.sms.repository.StudentRepository;
import com.example.sms.repository.TeacherRepository;
import com.example.sms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceLayerTest {

    // UserService dependencies
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // TeacherService dependencies
    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private TeacherService teacherService;

    // CustomUserDetailsService dependencies
    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    // StudentService (no mocks needed)
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService();
        lenient().when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded_" + invocation.getArgument(0));
    }

    // ===== UserService Tests =====

    @Test
    @DisplayName("Test 5: Create user with password encoding")
    void testCreateUserWithPasswordEncoding() {
        // Given
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword("encoded_pass123");
        savedUser.setRole(User.Role.ROLE_STUDENT);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser("testuser", "pass123", User.Role.ROLE_STUDENT);

        // Then
        assertNotNull(result);
        assertEquals("encoded_pass123", result.getPassword());
        verify(passwordEncoder, times(1)).encode("pass123");
    }

    @Test
    @DisplayName("Test 6: Verify teacher role is set correctly")
    void testCreateTeacherUserRole() {
        // Given
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.createUser("teacher", "pass", User.Role.ROLE_TEACHER);

        // Then
        assertEquals(User.Role.ROLE_TEACHER, result.getRole());
    }

    // ===== TeacherService Tests =====

    @Test
    @DisplayName("Test 7: Teacher creates student with ownership")
    void testTeacherCreatesStudent() {
        // Given
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setName("Dr. Jones");

        Student student = new Student();
        student.setRollNo("S100");
        student.setName("Alice");

        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Student result = teacherService.createStudent(student, teacher);

        // Then
        assertNotNull(result.getTeacher());
        assertEquals(teacher, result.getTeacher());
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    @DisplayName("Test 8: Save teacher to repository")
    void testSaveTeacher() {
        // Given
        Teacher teacher = new Teacher();
        teacher.setName("Prof. Brown");

        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        // When
        Teacher result = teacherService.saveTeacher(teacher);

        // Then
        assertEquals("Prof. Brown", result.getName());
        verify(teacherRepository, times(1)).save(teacher);
    }

    // ===== CustomUserDetailsService Tests =====

    @Test
    @DisplayName("Test 9: Load user by username successfully")
    void testLoadUserByUsername() {
        // Given
        User user = new User();
        user.setUsername("student1");
        user.setPassword("pass");
        user.setRole(User.Role.ROLE_STUDENT);

        when(userRepository.findByUsername("student1")).thenReturn(user);

        // When
        UserDetails result = userDetailsService.loadUserByUsername("student1");

        // Then
        assertNotNull(result);
        assertEquals("student1", result.getUsername());
    }

    @Test
    @DisplayName("Test 10: Throw exception when username not found")
    void testLoadUserByUsernameNotFound() {
        // Given
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown");
        });
    }
}
