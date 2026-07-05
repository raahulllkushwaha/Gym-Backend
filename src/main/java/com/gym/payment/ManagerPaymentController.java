package com.gym.payment;

import com.gym.common.ApiResponse;
import com.gym.payment.dto.RecordPaymentRequest;
import com.gym.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager/payments")
@RequiredArgsConstructor
public class ManagerPaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ApiResponse<?> recordPayment(@Valid @RequestBody RecordPaymentRequest req,
                                         @AuthenticationPrincipal CustomUserDetails manager) {
        Payment payment = paymentService.recordPayment(req, manager.getId());
        return ApiResponse.ok("Payment recorded. Invoice " + payment.getInvoiceNumber(), payment);
    }

    @GetMapping
    public ApiResponse<Page<Payment>> allPayments(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok("Payments", paymentService.allPayments(PageRequest.of(page, size)));
    }
}
