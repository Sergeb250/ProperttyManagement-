package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.Tenancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenancyRepository extends JpaRepository<Tenancy, Long> {
    List<Tenancy> findByTenantId(Long tenantId);

    List<Tenancy> findByPropertyId(Long propertyId);

    List<Tenancy> findByRoomId(Long roomId);

    List<Tenancy> findByActiveTrue();
}
