package com.gym.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByMemberUserIdAndDeletedFalseOrderByPaymentDateDesc(UUID memberUserId);

    Page<Payment> findByDeletedFalseOrderByPaymentDateDesc(Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentDate = :date AND p.deleted = false")
    BigDecimal sumRevenueByDate(LocalDate date);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end AND p.deleted = false")
    BigDecimal sumRevenueBetween(LocalDate start, LocalDate end);

    long countByPaymentDateBetween(LocalDate start, LocalDate end);
}
