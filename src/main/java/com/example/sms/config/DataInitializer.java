package com.example.sms.config;

import com.example.sms.entity.Student;
import com.example.sms.entity.Teacher;
import com.example.sms.entity.User;
import com.example.sms.repository.TeacherRepository;
import com.example.sms.repository.UserRepository;
import com.example.sms.service.TeacherService;
import com.example.sms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final UserService userService;
    private final TeacherService teacherService;

    public DataInitializer(UserRepository userRepository,
                           TeacherRepository teacherRepository,
                           UserService userService,
                           TeacherService teacherService) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.userService = userService;
        this.teacherService = teacherService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin teacher if missing
        createTeacherIfMissing("admin", "Admin123!", "Administrator");

        // Create a regular teacher account for use in demos/tests
        createTeacherIfMissing("teacher1", "Teacher123!", "Alice Teacher");

        // Create a student account linked to teacher1 if missing
        createStudentIfMissing("student1", "Student123!", "S001", "Bob Student", "teacher1");
    }

    private void createTeacherIfMissing(String username, String password, String displayName) {
        if (userRepository.findByUsername(username) == null) {
            User user = userService.createUser(username, password, User.Role.ROLE_TEACHER);

            // only save teacher if not already present for this user
            if (!teacherRepository.findByUser(user).isPresent()) {
                Teacher teacher = new Teacher();
                teacher.setName(displayName);
                teacher.setUser(user);
                teacherService.saveTeacher(teacher);
                log.info("Created teacher account -> username='{}' password='{}'", username, password);
            }
        }
    }

    private void createStudentIfMissing(String username, String password, String rollNo, String studentName, String teacherUsername) {
        if (userRepository.findByUsername(username) == null) {
            User studentUser = userService.createUser(username, password, User.Role.ROLE_STUDENT);

            // find teacher to assign
            User teacherUser = userRepository.findByUsername(teacherUsername);
            Teacher teacher = null;
            if (teacherUser != null) {
                teacher = teacherRepository.findByUser(teacherUser).orElse(null);
            }

            Student student = new Student();
            student.setRollNo(rollNo);
            student.setName(studentName);
            student.setUser(studentUser);

            if (teacher == null && teacherUser != null) {
                // create a teacher record for that user if it doesn't exist yet
                Teacher t = new Teacher();
                t.setUser(teacherUser);
                t.setName("(auto-assigned)");
                teacher = teacherService.saveTeacher(t);
            }

            if (teacher != null) {
                student.setTeacher(teacher);
                teacherService.createStudent(student, teacher);
            }

            log.info("Created student account -> username='{}' password='{}' (rollNo={})", username, password, rollNo);
        }
    }
}
