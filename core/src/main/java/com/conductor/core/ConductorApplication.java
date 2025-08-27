/*
*   ____                _            _
*  / ___|___  _ __   __| | ___ _   _| |_ ___  _ __
* | |   / _ \| '_ \ / _` |/ __| | | | __/ _ \| '__|
* | |__| (_) | | | | (_| | (__| |_| | || (_) | |
*  \____\___/|_| |_|\__,_|\___|\__,_|\__\___/|_|
*
* @Author: Ayush Srivastava
* @Version 1.0.0
* */
package com.conductor.core;

import com.conductor.core.model.user.User;
import com.conductor.core.model.user.UserRole;
import com.conductor.core.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(scanBasePackages = {
        "com.conductor.core",
        "com.conductor.adapters"
})
public class ConductorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConductorApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminUsername = "admin";
            // Check if admin user already exists
            userRepository.findByUsername(adminUsername).orElseGet(() -> {
                User admin = User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode("adminadmin")) // encode password
                        .firstName("admin")
                        .lastName("admin")
                        .emailAddress("admin@gmail.com")
                        .role(UserRole.ADMIN) // or your role setup
                        .build();
                System.out.println("Creating default admin user");
                return userRepository.save(admin);
            });
        };
    }
}
