package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.dto.RentRequestCreationDto;
import com.btech.propertymgt.demo.models.*;
import com.btech.propertymgt.demo.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingWorkflowService {

    private final RentRequestRepository rentRequestRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final LandlordRepository landlordRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public RentRequest submitRentRequest(RentRequestCreationDto dto) {
        Tenant tenant = tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        RentRequest rentRequest = new RentRequest();
        rentRequest.setTenant(tenant);
        rentRequest.setProperty(property);
        rentRequest.setLandlord(property.getLandlord());
        rentRequest.setRequestedMoveInDate(dto.getRequestedMoveInDate());
        rentRequest.setProposedRentAmount(dto.getProposedRentAmount());
        rentRequest.setMessage(dto.getMessage());

        try {
            rentRequest.setRequestSource(RentRequest.RequestSource.valueOf(dto.getRequestSource().toUpperCase()));
        } catch (Exception e) {
            rentRequest.setRequestSource(RentRequest.RequestSource.WEB);
        }

        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));
            // Enforce basic validation
            if (!room.getProperty().getId().equals(property.getId())) {
                throw new RuntimeException("Room does not belong to specified Property");
            }
            rentRequest.setRoom(room);
        }

        return rentRequestRepository.save(rentRequest);
    }

    @Transactional
    public RentRequest reviewRentRequest(Long requestId, String adminUsername, boolean approve, String notes) {
        RentRequest request = rentRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RentRequest.RentRequestStatus.SUBMITTED &&
                request.getStatus() != RentRequest.RentRequestStatus.UNDER_REVIEW) {
            throw new RuntimeException("Cannot review a request that is already processed.");
        }

        if (approve) {
            request.setStatus(RentRequest.RentRequestStatus.APPROVED);
            request.setApprovalNote(notes);
            // DO NOT automatically make Property OCCUPIED yet according to business rules.
            // Stays APPROVED -> AGREEMENT_PENDING
        } else {
            request.setStatus(RentRequest.RentRequestStatus.REJECTED);
            request.setRejectionReason(notes);
        }

        request.setReviewedBy(adminUsername);
        request.setReviewedAt(java.time.LocalDateTime.now());

        return rentRequestRepository.save(request);
    }
}
