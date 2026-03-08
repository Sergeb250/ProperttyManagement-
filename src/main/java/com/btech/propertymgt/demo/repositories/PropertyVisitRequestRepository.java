package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.PropertyVisitRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyVisitRequestRepository extends JpaRepository<PropertyVisitRequest, Long> {
    List<PropertyVisitRequest> findByLandlordId(Long landlordId);

    List<PropertyVisitRequest> findByTenantId(Long tenantId);

    List<PropertyVisitRequest> findByLandlordIdAndVisitStatus(Long landlordId,
            PropertyVisitRequest.VisitRequestStatus status);
}
