package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
    List<PaymentRequest> findByTenantId(Long tenantId);

    List<PaymentRequest> findByPropertyId(Long propertyId);

    Optional<PaymentRequest> findByPaymentLinkToken(String paymentLinkToken);
}
