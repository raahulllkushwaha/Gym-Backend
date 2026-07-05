package com.gym.payment.dto;

import com.gym.payment.PaymentMode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordPaymentRequest(
        @NotNull UUID memberUserId,
        @NotNull @PositiveOrZero BigDecimal amount,
        BigDecimal discount,
        BigDecimal gst,
        BigDecimal remainingBalance,
        @NotNull PaymentMode paymentMode,
        String transactionId,
        boolean renewMembership,   // if true, extends membership from plan's duration
        UUID renewalPlanId         // optional - switch plan on renewal, else keeps current plan
) {}
