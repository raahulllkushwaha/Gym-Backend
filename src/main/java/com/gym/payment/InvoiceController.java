package com.gym.payment;

import com.gym.security.CustomUserDetails;
import com.gym.user.User;
import com.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InvoiceController {

    private final PaymentService paymentService;
    private final InvoicePdfService invoicePdfService;
    private final UserRepository userRepository;

    // Manager - download any member's invoice
    @GetMapping("/api/manager/payments/{paymentId}/invoice")
    public ResponseEntity<byte[]> downloadForManager(@PathVariable UUID paymentId) {
        Payment payment = paymentService.getById(paymentId);
        User member = userRepository.findById(payment.getMemberUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return pdfResponse(payment, member);
    }

    // Member - download own invoice only
    @GetMapping("/api/members/me/invoices/{paymentId}")
    public ResponseEntity<byte[]> downloadForMember(@PathVariable UUID paymentId,
                                                     @AuthenticationPrincipal CustomUserDetails principal) {
        Payment payment = paymentService.getById(paymentId);
        if (!payment.getMemberUserId().equals(principal.getId())) {
            throw new AccessDeniedException("You cannot access another member's invoice");
        }
        User member = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return pdfResponse(payment, member);
    }

    // Member - list own payment history
    @GetMapping("/api/members/me/payments")
    public com.gym.common.ApiResponse<List<Payment>> myPayments(@AuthenticationPrincipal CustomUserDetails principal) {
        return com.gym.common.ApiResponse.ok("Payment history", paymentService.historyForMember(principal.getId()));
    }

    private ResponseEntity<byte[]> pdfResponse(Payment payment, User member) {
        byte[] pdf = invoicePdfService.generate(payment, member);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(payment.getInvoiceNumber() + ".pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
