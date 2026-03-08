package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
    List<Agreement> findByTenantId(Long tenantId);

    List<Agreement> findByPropertyId(Long propertyId);
}
