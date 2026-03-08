package com.btech.propertymgt.demo.controllers;

import com.btech.propertymgt.demo.models.Agreement;
import com.btech.propertymgt.demo.services.AgreementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agreements")
@RequiredArgsConstructor
public class AgreementController {

    private final AgreementService agreementService;

    @PostMapping
    public ResponseEntity<Agreement> createAgreement(@RequestBody Agreement agreement) {
        return ResponseEntity.ok(agreementService.createAgreement(agreement));
    }

    @GetMapping
    public ResponseEntity<List<Agreement>> getAllAgreements() {
        return ResponseEntity.ok(agreementService.getAllAgreements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agreement> getAgreementById(@PathVariable Long id) {
        return agreementService.getAgreementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Agreement>> getAgreementsByTenant(@PathVariable Long tenantId) {
        return ResponseEntity.ok(agreementService.getAgreementsByTenant(tenantId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Agreement> updateAgreementStatus(
            @PathVariable Long id,
            @RequestParam Agreement.AgreementStatus status) {
        try {
            return ResponseEntity.ok(agreementService.updateAgreementStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgreement(@PathVariable Long id) {
        agreementService.deleteAgreement(id);
        return ResponseEntity.ok().build();
    }
}
