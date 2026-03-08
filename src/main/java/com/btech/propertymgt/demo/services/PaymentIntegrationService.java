package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.dto.PaymentInitiationDto;
import com.btech.propertymgt.demo.models.PaymentRequest;
import com.btech.propertymgt.demo.models.PaymentTransaction;
import com.btech.propertymgt.demo.repositories.PaymentRequestRepository;
import com.btech.propertymgt.demo.repositories.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentIntegrationService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Transactional
    public PaymentRequest generatePaymentLink(PaymentRequest request) {
        String token = UUID.randomUUID().toString();
        request.setPaymentLinkToken("https://propertymgt.example.com/pay/" + token);
        request.setStatus(PaymentRequest.PaymentRequestStatus.SENT);
        return paymentRequestRepository.save(request);
    }

    @Transactional
    public PaymentTransaction initiatePayment(PaymentInitiationDto dto) {
        PaymentRequest request = paymentRequestRepository.findById(dto.getPaymentRequestId())
                .orElseThrow(() -> new RuntimeException("Payment Request missing"));

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setPaymentRequest(request);
        transaction.setAmount(request.getAmount());
        transaction.setMethod(dto.getMethod());
        transaction.setProvider(dto.getProvider());
        transaction.setStatus(PaymentTransaction.PaymentTransactionStatus.INITIATED);

        return paymentTransactionRepository.save(transaction);
    }
}
