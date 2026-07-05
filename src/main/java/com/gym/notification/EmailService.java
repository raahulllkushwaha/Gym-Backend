package com.gym.notification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.gym.name}")
    private String gymName;

    @Async
    public void sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, gymName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} subject='{}'", to, subject);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendWelcomeCredentials(String to, String fullName, String username, String rawPassword,
                                        String membershipPlan, String trainerName, String expiryDate) {
        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
                  <h2>Welcome to %s 💪</h2>
                  <p>Hello %s,</p>
                  <p>Your membership has been activated successfully.</p>
                  <table style="border-collapse:collapse">
                    <tr><td><b>Username</b></td><td>%s</td></tr>
                    <tr><td><b>Password</b></td><td>%s</td></tr>
                    <tr><td><b>Membership</b></td><td>%s</td></tr>
                    <tr><td><b>Trainer</b></td><td>%s</td></tr>
                    <tr><td><b>Expiry Date</b></td><td>%s</td></tr>
                  </table>
                  <p style="color:#b91c1c">Please change your password after first login.</p>
                  <p>Thank You.</p>
                </div>
                """.formatted(gymName, fullName, username, rawPassword, membershipPlan, trainerName, expiryDate);
        sendHtml(to, "Welcome to " + gymName + " - Your Login Details", html);
    }

    public void sendMembershipExpiryReminder(String to, String fullName, String expiryDate, int daysLeft) {
        String html = """
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
                  <h2>Membership Expiry Reminder</h2>
                  <p>Hello %s,</p>
                  <p>Your gym membership expires in <b>%d day(s)</b> (on %s).</p>
                  <p>Please renew to continue enjoying our facilities.</p>
                  <p>Thank You.</p>
                </div>
                """.formatted(fullName, daysLeft, expiryDate);
        sendHtml(to, gymName + " - Membership Expiring Soon", html);
    }
}
