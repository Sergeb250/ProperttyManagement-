package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.RentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRequestRepository extends JpaRepository<RentRequest, Long> {
    List<RentRequest> findByTenantId(Long tenantId);

    List<RentRequest> findByLandlordId(Long landlordId);

    List<RentRequest> findByLandlordIdAndStatus(Long landlordId, RentRequest.RentRequestStatus status);

    List<RentRequest> findByPropertyId(Long propertyId);
}
