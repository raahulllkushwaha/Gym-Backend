package com.gym.reports;

import com.gym.membership.MembershipRepository;
import com.gym.membership.MembershipStatus;
import com.gym.payment.PaymentRepository;
import com.gym.user.Role;
import com.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public Map<String, Object> dashboardSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();

        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);

        BigDecimal todayRevenue = paymentRepository.sumRevenueByDate(today);
        BigDecimal monthRevenue = paymentRepository.sumRevenueBetween(thisMonth.atDay(1), thisMonth.atEndOfMonth());
        BigDecimal yearRevenue = paymentRepository.sumRevenueBetween(
                LocalDate.of(today.getYear(), 1, 1), LocalDate.of(today.getYear(), 12, 31));

        long activeMembers = membershipRepository.findAll().stream()
                .filter(m -> !m.isDeleted() && m.getStatus() == MembershipStatus.ACTIVE).count();
        long expiredMembers = membershipRepository.findAll().stream()
                .filter(m -> !m.isDeleted() && m.getStatus() == MembershipStatus.EXPIRED).count();
        long totalTrainers = userRepository.findAll().stream()
                .filter(u -> !u.isDeleted() && u.getRole() == Role.TRAINER).count();
        long totalMembers = userRepository.findAll().stream()
                .filter(u -> !u.isDeleted() && u.getRole() == Role.MEMBER).count();

        summary.put("todayRevenue", todayRevenue);
        summary.put("monthRevenue", monthRevenue);
        summary.put("yearRevenue", yearRevenue);
        summary.put("activeMembers", activeMembers);
        summary.put("expiredMembers", expiredMembers);
        summary.put("totalMembers", totalMembers);
        summary.put("totalTrainers", totalTrainers);
        summary.put("newPaymentsToday", paymentRepository.countByPaymentDateBetween(today, today));

        return summary;
    }

    public Map<String, Object> revenueBetween(LocalDate start, LocalDate end) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("startDate", start);
        result.put("endDate", end);
        result.put("totalRevenue", paymentRepository.sumRevenueBetween(start, end));
        result.put("transactionCount", paymentRepository.countByPaymentDateBetween(start, end));
        return result;
    }

    public Map<String, Object> membershipStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        var all = membershipRepository.findAll().stream().filter(m -> !m.isDeleted()).toList();

        stats.put("total", all.size());
        stats.put("active", all.stream().filter(m -> m.getStatus() == MembershipStatus.ACTIVE).count());
        stats.put("expired", all.stream().filter(m -> m.getStatus() == MembershipStatus.EXPIRED).count());
        stats.put("frozen", all.stream().filter(m -> m.getStatus() == MembershipStatus.FROZEN).count());
        stats.put("cancelled", all.stream().filter(m -> m.getStatus() == MembershipStatus.CANCELLED).count());
        stats.put("expiringIn7Days", all.stream()
                .filter(m -> m.getStatus() == MembershipStatus.ACTIVE)
                .filter(m -> !m.getExpiryDate().isBefore(LocalDate.now()) && !m.getExpiryDate().isAfter(LocalDate.now().plusDays(7)))
                .count());
        return stats;
    }
}
