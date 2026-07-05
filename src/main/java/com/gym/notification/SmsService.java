package com.gym.notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

    @Value("${app.twilio.account-sid}")
    private String accountSid;

    @Value("${app.twilio.auth-token}")
    private String authToken;

    @Value("${app.twilio.from-number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.isBlank()) {
            Twilio.init(accountSid, authToken);
        } else {
            log.warn("Twilio credentials not configured - SMS sending disabled (dev mode)");
        }
    }

    @Async
    public void send(String toPhoneNumber, String body) {
        if (accountSid == null || accountSid.isBlank()) {
            log.info("[DEV-SMS] to={} body={}", toPhoneNumber, body);
            return;
        }
        try {
            Message.creator(
                    new PhoneNumber("+91" + toPhoneNumber),
                    new PhoneNumber(fromNumber),
                    body
            ).create();
            log.info("SMS sent to {}", toPhoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());
        }
    }

    public void sendWelcomeCredentials(String toPhoneNumber, String gymName, String username, String rawPassword) {
        String body = "Welcome to %s.\nUsername: %s\nPassword: %s\nHappy Workout!".formatted(gymName, username, rawPassword);
        send(toPhoneNumber, body);
    }

    public void sendExpiryReminder(String toPhoneNumber, String gymName, String expiryDate) {
        String body = "%s Reminder: Your membership expires on %s. Renew soon. Thank You.".formatted(gymName, expiryDate);
        send(toPhoneNumber, body);
    }
}
