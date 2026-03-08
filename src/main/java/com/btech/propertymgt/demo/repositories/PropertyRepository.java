package com.btech.propertymgt.demo.repositories;

import com.btech.propertymgt.demo.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByStatus(Property.PropertyStatus status);

    List<Property> findByLocationId(Long locationId);

    List<Property> findByLandlordId(Long landlordId);

    List<Property> findByTypeAndStatus(Property.PropertyType type, Property.PropertyStatus status);

    // Public Marketplace Queries
    List<Property> findByStatusAndListingStatus(Property.PropertyStatus status, Property.ListingStatus listingStatus);
}
