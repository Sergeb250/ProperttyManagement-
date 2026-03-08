package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.dto.PropertyMarketplaceResponseDto;
import com.btech.propertymgt.demo.models.Property;
import com.btech.propertymgt.demo.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final PropertyRepository propertyRepository;

    @Transactional(readOnly = true)
    public List<PropertyMarketplaceResponseDto> getAvailableListings() {
        // Find visible properties
        List<Property> properties = propertyRepository.findByStatusAndListingStatus(
                Property.PropertyStatus.AVAILABLE,
                Property.ListingStatus.PUBLISHED);

        List<Property> partiallyOccupied = propertyRepository.findByStatusAndListingStatus(
                Property.PropertyStatus.PARTIALLY_OCCUPIED,
                Property.ListingStatus.PUBLISHED);

        properties.addAll(partiallyOccupied);

        return properties.stream()
                .map(PropertyMarketplaceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PropertyMarketplaceResponseDto getListingDetails(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if ((property.getStatus() != Property.PropertyStatus.AVAILABLE
                && property.getStatus() != Property.PropertyStatus.PARTIALLY_OCCUPIED)
                || property.getListingStatus() != Property.ListingStatus.PUBLISHED) {
            throw new RuntimeException("Property is not available on marketplace currently");
        }

        return PropertyMarketplaceResponseDto.fromEntity(property);
    }
}
