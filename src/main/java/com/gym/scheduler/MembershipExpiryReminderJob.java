package com.gym.scheduler;

import com.gym.membership.Membership;
import com.gym.membership.MembershipRepository;
import com.gym.membership.MembershipStatus;
import com.gym.notification.EmailService;
import com.gym.notification.NotificationService;
import com.gym.notification.SmsService;
import com.gym.user.User;
import com.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Runs every morning (see QuartzConfig for cron). Two independent checks:
 *  1) Every membership expiring in exactly 3 days -> email + sms reminder.
 *  2) Long-term (yearly) plans expiring in exactly 30 days -> early reminder.
 * Also flips ACTIVE memberships whose expiry date has already passed to EXPIRED.
 * All done idempotently via reminder3DaySent / reminder30DaySent flags so nobody gets spammed.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipExpiryReminderJob extends QuartzJobBean {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationService notificationService;

    @Value("${app.gym.name}")
    private String gymName;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Running MembershipExpiryReminderJob at {}", LocalDate.now());

        send3DayReminders();
        send30DayReminders();
        expireOverdueMemberships();
    }

    private void send3DayReminders() {
        LocalDate targetDate = LocalDate.now().plusDays(3);
        List<Membership> memberships = membershipRepository
                .findByStatusAndExpiryDateAndReminder3DaySentFalse(MembershipStatus.ACTIVE, targetDate);

        for (Membership m : memberships) {
            notifyMember(m, 3);
            m.setReminder3DaySent(true);
            membershipRepository.save(m);
        }
        log.info("Sent {} three-day expiry reminders", memberships.size());
    }

    private void send30DayReminders() {
        LocalDate targetDate = LocalDate.now().plusDays(30);
        List<Membership> memberships = membershipRepository
                .findByStatusAndExpiryDateAndReminder30DaySentFalse(MembershipStatus.ACTIVE, targetDate)
                .stream()
                .filter(m -> m.getPlan().isLongTermPlan())
                .toList();

        for (Membership m : memberships) {
            notifyMember(m, 30);
            m.setReminder30DaySent(true);
            membershipRepository.save(m);
        }
        log.info("Sent {} thirty-day expiry reminders (yearly plans)", memberships.size());
    }

    private void expireOverdueMemberships() {
        List<Membership> overdue = membershipRepository
                .findByStatusAndExpiryDateBefore(MembershipStatus.ACTIVE, LocalDate.now());
        for (Membership m : overdue) {
            m.setStatus(MembershipStatus.EXPIRED);
            membershipRepository.save(m);
        }
        log.info("Marked {} memberships as EXPIRED", overdue.size());
    }

    private void notifyMember(Membership membership, int daysLeft) {
        User member = userRepository.findById(membership.getMemberUserId()).orElse(null);
        if (member == null) return;

        String expiryDate = membership.getExpiryDate().toString();
        emailService.sendMembershipExpiryReminder(member.getEmail(), member.getFullName(), expiryDate, daysLeft);
        smsService.sendExpiryReminder(member.getPhoneNumber(), gymName, expiryDate);
        notificationService.create(member.getId(), "Membership Expiring Soon",
                "Your membership expires in " + daysLeft + " day(s) on " + expiryDate + ". Please renew.");
    }
}
