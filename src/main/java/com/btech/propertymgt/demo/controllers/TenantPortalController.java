package com.btech.propertymgt.demo.controllers;

import com.btech.propertymgt.demo.dto.PaymentInitiationDto;
import com.btech.propertymgt.demo.dto.RentRequestCreationDto;
import com.btech.propertymgt.demo.models.PaymentTransaction;
import com.btech.propertymgt.demo.models.RentRequest;
import com.btech.propertymgt.demo.services.BookingWorkflowService;
import com.btech.propertymgt.demo.services.PaymentIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenant")
@RequiredArgsConstructor
public class TenantPortalController {

    private final BookingWorkflowService bookingWorkflowService;
    private final PaymentIntegrationService paymentIntegrationService;

    @PostMapping("/rent-request")
    public ResponseEntity<RentRequest> submitRentRequest(@RequestBody RentRequestCreationDto dto) {
        try {
            return ResponseEntity.ok(bookingWorkflowService.submitRentRequest(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentTransaction> initiatePayment(@RequestBody PaymentInitiationDto dto) {
        try {
            return ResponseEntity.ok(paymentIntegrationService.initiatePayment(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
