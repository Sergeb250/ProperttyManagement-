package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandlordRepository extends JpaRepository<Landlord, Long> {
}
