package com.example.sms.repository;

import com.example.sms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // Custom query method
    User findByUsername(String username);
}
