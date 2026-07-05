package com.gym.user;

import com.gym.common.CredentialGenerator;
import com.gym.notification.EmailService;
import com.gym.notification.NotificationService;
import com.gym.notification.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Central place where login credentials get created. No public self-signup ever calls this
 * directly - it is only triggered by: Manager creating a Trainer/Member, or Manager approving
 * a pending SignupRequest. Always sends the plaintext password via email + SMS exactly once,
 * never stores it anywhere after this call returns.
 */
@Service
@RequiredArgsConstructor
public class UserProvisioningService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialGenerator credentialGenerator;
    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationService notificationService;

    @Transactional
    public User provisionUser(String fullName, String email, String phoneNumber, Role role, UUID createdByUserId) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("A user with this email already exists");
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalStateException("A user with this phone number already exists");
        }

        String username = generateUniqueUsername(fullName);
        String rawPassword = credentialGenerator.generatePassword();

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .status(UserStatus.ACTIVE)
                .mustChangePassword(true)
                .createdByUserId(createdByUserId)
                .build();

        user = userRepository.save(user);

        // fire-and-forget notifications (async)
        emailService.sendWelcomeCredentials(email, fullName, username, rawPassword, "-", "-", "-");
        smsService.sendWelcomeCredentials(phoneNumber, "Gym", username, rawPassword);
        notificationService.create(user.getId(), "Welcome!", "Your account has been created. Please change your password after first login.");

        return user;
    }

    private String generateUniqueUsername(String fullName) {
        String candidate;
        int attempts = 0;
        do {
            candidate = credentialGenerator.generateUsername(fullName);
            attempts++;
        } while (userRepository.existsByUsername(candidate) && attempts < 20);
        return candidate;
    }
}
