package com.btech.propertymgt.demo.controllers;

import com.btech.propertymgt.demo.models.PropertyImage;
import com.btech.propertymgt.demo.models.RentRequest;
import com.btech.propertymgt.demo.models.Tenancy;
import com.btech.propertymgt.demo.repositories.PropertyRepository;
import com.btech.propertymgt.demo.services.BookingWorkflowService;
import com.btech.propertymgt.demo.services.FileStorageService;
import com.btech.propertymgt.demo.services.ManualTenancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/landlord")
@RequiredArgsConstructor
public class LandlordDashboardController {

    private final BookingWorkflowService bookingWorkflowService;
    private final ManualTenancyService manualTenancyService;
    private final FileStorageService fileStorageService;
    private final PropertyRepository propertyRepository;

    @PostMapping("/rent-requests/{id}/review")
    public ResponseEntity<RentRequest> reviewRequest(
            @PathVariable Long id,
            @RequestParam boolean approve,
            @RequestParam(required = false) String notes) {
        // Authenticated Landlord Username logic would exist here
        String adminUsername = "LANDLORD_ADMIN";
        return ResponseEntity.ok(bookingWorkflowService.reviewRentRequest(id, adminUsername, approve, notes));
    }

    @PostMapping("/manual-tenancy")
    public ResponseEntity<Tenancy> createManualTenancy(
            @RequestParam Long landlordId,
            @RequestParam Long tenantId,
            @RequestParam Long propertyId,
            @RequestParam(required = false) Long roomId,
            @RequestParam String moveInDate) {
        LocalDate date = LocalDate.parse(moveInDate);
        return ResponseEntity
                .ok(manualTenancyService.createManualTenancy(landlordId, tenantId, propertyId, roomId, date));
    }

    @PostMapping("/properties/{id}/images")
    public ResponseEntity<PropertyImage> uploadPropertyImage(
            @PathVariable Long id,
            @RequestParam String fileName,
            @RequestParam String contentType,
            @RequestParam String category,
            @RequestParam boolean isCover) {
        return propertyRepository.findById(id)
                .map(property -> ResponseEntity
                        .ok(fileStorageService.uploadPropertyImage(property, fileName, contentType, category, isCover)))
                .orElse(ResponseEntity.notFound().build());
    }
}
