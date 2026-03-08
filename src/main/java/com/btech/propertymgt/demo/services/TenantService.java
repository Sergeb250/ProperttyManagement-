package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.Tenant;
import com.btech.propertymgt.demo.repositories.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    @Transactional
    public Tenant updateTenant(Long id, Tenant tenantDetails) {
        return tenantRepository.findById(id).map(tenant -> {
            tenant.setPreferredPaymentMethod(tenantDetails.getPreferredPaymentMethod());
            tenant.setMomoPayNumber(tenantDetails.getMomoPayNumber());
            tenant.setCardNumber(tenantDetails.getCardNumber());

            // Propagate profile details if provided
            if (tenantDetails.getUser() != null) {
                com.btech.propertymgt.demo.models.User existingUser = tenant.getUser();
                com.btech.propertymgt.demo.models.User incomingUser = tenantDetails.getUser();

                if (incomingUser.getFirstName() != null)
                    existingUser.setFirstName(incomingUser.getFirstName());
                if (incomingUser.getLastName() != null)
                    existingUser.setLastName(incomingUser.getLastName());
                if (incomingUser.getPhoneNumber() != null)
                    existingUser.setPhoneNumber(incomingUser.getPhoneNumber());
                if (incomingUser.getProfileImageUrl() != null)
                    existingUser.setProfileImageUrl(incomingUser.getProfileImageUrl());
            }

            return tenantRepository.save(tenant);
        }).orElseThrow(() -> new RuntimeException("Tenant not found with id " + id));
    }

    public void deleteTenant(Long id) {
        tenantRepository.deleteById(id);
    }
}
