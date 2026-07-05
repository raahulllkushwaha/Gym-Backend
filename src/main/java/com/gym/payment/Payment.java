package com.gym.payment;

import com.gym.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private UUID memberUserId;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // e.g. INV-2026-000123

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // final amount actually paid

    @Column(precision = 10, scale = 2)
    @lombok.Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @lombok.Builder.Default
    private BigDecimal gst = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @lombok.Builder.Default
    private BigDecimal remainingBalance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMode paymentMode;

    private String transactionId; // for UPI/card/bank transfer references, null for cash

    @Column(nullable = false)
    private LocalDate paymentDate;

    private String planNameSnapshot; // captured at time of payment for invoice history integrity

    private String recordedByUserId; // manager who recorded it (stored as string UUID for simplicity in reports)
}
