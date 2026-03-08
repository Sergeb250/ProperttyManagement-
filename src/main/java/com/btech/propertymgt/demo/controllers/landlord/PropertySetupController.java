package com.btech.propertymgt.demo.controllers.landlord;

import com.btech.propertymgt.demo.dtos.requests.PropertySetupRequest;
import com.btech.propertymgt.demo.dtos.responses.PropertySetupResponse;
import com.btech.propertymgt.demo.services.PropertySetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/api/v1/landlord/properties")
@RequiredArgsConstructor
public class PropertySetupController {

    private final PropertySetupService propertySetupService;

    @PostMapping(value = "/setup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropertySetupResponse> setupMarketplaceProperty(
            @RequestPart("data") PropertySetupRequest data,
            MultipartHttpServletRequest request) {

        PropertySetupResponse response = propertySetupService.createFullPropertySetup(data, request);

        if (!response.isCompletedSetup() || !response.getWarnings().isEmpty()) {
            // Return 202 Accepted if it saved but with warnings
            return ResponseEntity.accepted().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
