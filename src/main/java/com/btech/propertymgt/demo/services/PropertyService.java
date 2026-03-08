package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.Property;

import com.btech.propertymgt.demo.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public List<Property> getAvailableProperties() {
        return propertyRepository.findByStatus(Property.PropertyStatus.AVAILABLE);
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        return propertyRepository.findById(id).map(property -> {
            property.setTitle(propertyDetails.getTitle());
            property.setDescription(propertyDetails.getDescription());
            property.setPrice(propertyDetails.getPrice());
            property.setAddress(propertyDetails.getAddress());
            property.setType(propertyDetails.getType());
            property.setStatus(propertyDetails.getStatus());
            property.setLocation(propertyDetails.getLocation());
            property.setLatitude(propertyDetails.getLatitude());
            property.setLongitude(propertyDetails.getLongitude());
            return propertyRepository.save(property);
        }).orElseThrow(() -> new RuntimeException("Property not found with id " + id));
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}
