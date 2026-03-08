package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.Location;
import com.btech.propertymgt.demo.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public List<Location> getSubLocations(Long parentId) {
        return locationRepository.findByParentId(parentId);
    }

    public List<Location> getLocationsByType(Location.LocationType type) {
        return locationRepository.findByType(type);
    }

    public Location updateLocation(Long id, Location locationDetails) {
        return locationRepository.findById(id).map(location -> {
            location.setName(locationDetails.getName());
            location.setType(locationDetails.getType());
            location.setParentLocation(locationDetails.getParentLocation());
            return locationRepository.save(location);
        }).orElseThrow(() -> new RuntimeException("Location not found with id " + id));
    }

    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
