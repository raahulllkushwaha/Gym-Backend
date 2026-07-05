package com.gym.config;

import com.gym.user.Role;
import com.gym.user.User;
import com.gym.user.UserRepository;
import com.gym.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * There is no public registration endpoint for MANAGER (by design - see SecurityConfig).
 * So the very first manager account must come from somewhere. This runs once on startup:
 * if zero MANAGER users exist yet, create one from env vars (never hardcoded in source).
 *
 * Set these before first run:
 *   BOOTSTRAP_MANAGER_USERNAME, BOOTSTRAP_MANAGER_PASSWORD,
 *   BOOTSTRAP_MANAGER_EMAIL, BOOTSTRAP_MANAGER_PHONE
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.manager-username:}")
    private String bootstrapUsername;

    @Value("${app.bootstrap.manager-password:}")
    private String bootstrapPassword;

    @Value("${app.bootstrap.manager-email:}")
    private String bootstrapEmail;

    @Value("${app.bootstrap.manager-phone:}")
    private String bootstrapPhone;

    @Override
    public void run(String... args) {
        boolean managerExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == Role.MANAGER && !u.isDeleted());

        if (managerExists) {
            log.info("Manager account already exists - skipping bootstrap");
            return;
        }

        if (bootstrapUsername.isBlank() || bootstrapPassword.isBlank() || bootstrapEmail.isBlank() || bootstrapPhone.isBlank()) {
            log.warn("No MANAGER account exists and BOOTSTRAP_MANAGER_* env vars are not set. " +
                    "Set BOOTSTRAP_MANAGER_USERNAME, BOOTSTRAP_MANAGER_PASSWORD, BOOTSTRAP_MANAGER_EMAIL, " +
                    "BOOTSTRAP_MANAGER_PHONE and restart, otherwise nobody can log in.");
            return;
        }

        User manager = User.builder()
                .username(bootstrapUsername)
                .password(passwordEncoder.encode(bootstrapPassword))
                .fullName("Gym Manager")
                .email(bootstrapEmail)
                .phoneNumber(bootstrapPhone)
                .role(Role.MANAGER)
                .status(UserStatus.ACTIVE)
                .mustChangePassword(true)
                .build();

        userRepository.save(manager);
        log.info("Bootstrap MANAGER account created with username '{}'. Please log in and change the password immediately.", bootstrapUsername);
    }
}
