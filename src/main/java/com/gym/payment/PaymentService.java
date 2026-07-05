package com.gym.payment;

import com.gym.membership.Membership;
import com.gym.membership.MembershipPlan;
import com.gym.membership.MembershipPlanRepository;
import com.gym.membership.MembershipRepository;
import com.gym.membership.MembershipStatus;
import com.gym.payment.dto.RecordPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository planRepository;

    @Transactional
    public Payment recordPayment(RecordPaymentRequest req, UUID recordedByManagerId) {
        Membership membership = membershipRepository.findByMemberUserIdAndDeletedFalse(req.memberUserId())
                .orElseThrow(() -> new IllegalStateException("No membership found for this member"));

        MembershipPlan planForSnapshot = membership.getPlan();

        if (req.renewMembership()) {
            MembershipPlan renewalPlan = req.renewalPlanId() != null
                    ? planRepository.findById(req.renewalPlanId())
                        .orElseThrow(() -> new IllegalArgumentException("Renewal plan not found"))
                    : membership.getPlan();

            // extend from whichever is later: today or current expiry (so renewing early doesn't lose paid days)
            LocalDate base = membership.getExpiryDate().isAfter(LocalDate.now())
                    ? membership.getExpiryDate() : LocalDate.now();

            membership.setPlan(renewalPlan);
            membership.setExpiryDate(base.plusDays(renewalPlan.getDurationInDays()));
            membership.setStatus(MembershipStatus.ACTIVE);
            membership.setReminder3DaySent(false);
            membership.setReminder30DaySent(false);
            membershipRepository.save(membership);
            planForSnapshot = renewalPlan;
        }

        Payment payment = Payment.builder()
                .memberUserId(req.memberUserId())
                .invoiceNumber(generateInvoiceNumber())
                .amount(req.amount())
                .discount(req.discount() != null ? req.discount() : BigDecimal.ZERO)
                .gst(req.gst() != null ? req.gst() : BigDecimal.ZERO)
                .remainingBalance(req.remainingBalance() != null ? req.remainingBalance() : BigDecimal.ZERO)
                .paymentMode(req.paymentMode())
                .transactionId(req.transactionId())
                .paymentDate(LocalDate.now())
                .planNameSnapshot(planForSnapshot.getName())
                .recordedByUserId(recordedByManagerId.toString())
                .build();

        return paymentRepository.save(payment);
    }

    public List<Payment> historyForMember(UUID memberUserId) {
        return paymentRepository.findByMemberUserIdAndDeletedFalseOrderByPaymentDateDesc(memberUserId);
    }

    public Page<Payment> allPayments(Pageable pageable) {
        return paymentRepository.findByDeletedFalseOrderByPaymentDateDesc(pageable);
    }

    public Payment getById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    private String generateInvoiceNumber() {
        int year = Year.now().getValue();
        long countThisYear = paymentRepository.count() + 1; // simple monotonic counter, fine for single-branch gym
        return "INV-%d-%06d".formatted(year, countThisYear);
    }
}
