package com.gmail.detection.config;

import com.gmail.detection.entity.User;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.Role;
import com.gmail.detection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ensures a default ADMIN account exists on first startup so the system is
 * usable immediately, without a chicken-and-egg problem where every endpoint
 * that creates users requires an admin to already be logged in.
 *
 * Configure via app.admin.* properties. Change the seeded password after
 * first login.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.first-name}")
    private String adminFirstName;

    @Value("${app.admin.last-name}")
    private String adminLastName;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        boolean adminExists = userRepository.findByRole(Role.ADMIN)
                .stream()
                .anyMatch(User::isActive);

        if (adminExists) {
            return;
        }

        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User admin = new User();
        admin.setFirstName(adminFirstName);
        admin.setLastName(adminLastName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setDepartment(DepartmentType.ADMINISTRATION);
        admin.setActive(true);
        admin.setEmailVerified(true);

        userRepository.save(admin);

        log.info("==============================================================");
        log.info(" Seeded default ADMIN account -> email: {} / password: {}", adminEmail, adminPassword);
        log.info(" Please log in and change this password, or override app.admin.* properties.");
        log.info("==============================================================");
    }
}
