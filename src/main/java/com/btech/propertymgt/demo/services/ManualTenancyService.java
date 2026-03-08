package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.*;
import com.btech.propertymgt.demo.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ManualTenancyService {

    private final TenancyRepository tenancyRepository;
    private final TenantRepository tenantRepository;
    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public Tenancy createManualTenancy(Long landlordId, Long tenantId, Long propertyId, Long roomId,
            LocalDate moveInDate) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new RuntimeException("Tenant not found"));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("Property does not belong to authorized landlord");
        }

        Tenancy tenancy = new Tenancy();
        tenancy.setTenant(tenant);
        tenancy.setProperty(property);
        tenancy.setMoveInDate(moveInDate);
        tenancy.setStatus(Tenancy.TenancyStatus.ACTIVE);
        tenancy.setActive(true);

        if (roomId != null) {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
            if (!room.getProperty().getId().equals(propertyId)) {
                throw new RuntimeException("Room does not belong to the property");
            }
            tenancy.setRoom(room);
            room.setRoomStatus(Room.RoomStatus.OCCUPIED);
            roomRepository.save(room);
            property.setStatus(Property.PropertyStatus.PARTIALLY_OCCUPIED);
        } else {
            property.setStatus(Property.PropertyStatus.OCCUPIED);
        }

        propertyRepository.save(property);
        return tenancyRepository.save(tenancy);
    }
}
