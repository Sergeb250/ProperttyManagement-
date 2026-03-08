package com.btech.propertymgt.demo.controllers;

import com.btech.propertymgt.demo.models.PaymentTransaction;
import com.btech.propertymgt.demo.models.PaymentRequest;
import com.btech.propertymgt.demo.repositories.PaymentTransactionRepository;
import com.btech.propertymgt.demo.repositories.PaymentRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1/webhooks/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentTransactionRepository transactionRepository;
    private final PaymentRequestRepository requestRepository;

    @PostMapping("/callback")
    @Transactional
    public ResponseEntity<String> handlePaymentCallback(
            @RequestParam String externalTransactionId,
            @RequestParam String status) {

        PaymentTransaction transaction = transactionRepository.findByExternalTransactionId(externalTransactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if ("SUCCESS".equalsIgnoreCase(status)) {
            transaction.setStatus(PaymentTransaction.PaymentTransactionStatus.SUCCESS);

            PaymentRequest parentRequest = transaction.getPaymentRequest();
            parentRequest.setStatus(PaymentRequest.PaymentRequestStatus.PAID);

            requestRepository.save(parentRequest);
        } else {
            transaction.setStatus(PaymentTransaction.PaymentTransactionStatus.FAILED);
        }

        transactionRepository.save(transaction);
        return ResponseEntity.ok("ACK");
    }
}
