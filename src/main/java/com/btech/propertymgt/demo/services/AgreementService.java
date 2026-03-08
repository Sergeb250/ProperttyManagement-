package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.Agreement;
import com.btech.propertymgt.demo.repositories.AgreementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgreementService {

    private final AgreementRepository agreementRepository;

    public Agreement createAgreement(Agreement agreement) {
        return agreementRepository.save(agreement);
    }

    public List<Agreement> getAllAgreements() {
        return agreementRepository.findAll();
    }

    public Optional<Agreement> getAgreementById(Long id) {
        return agreementRepository.findById(id);
    }

    public List<Agreement> getAgreementsByTenant(Long tenantId) {
        return agreementRepository.findByTenantId(tenantId);
    }

    public Agreement updateAgreementStatus(Long id, Agreement.AgreementStatus status) {
        return agreementRepository.findById(id).map(agreement -> {
            agreement.setStatus(status);
            return agreementRepository.save(agreement);
        }).orElseThrow(() -> new RuntimeException("Agreement not found with id " + id));
    }

    public void deleteAgreement(Long id) {
        agreementRepository.deleteById(id);
    }
}
