package com.btech.propertymgt.demo.controllers;

import com.btech.propertymgt.demo.dto.PropertyMarketplaceResponseDto;
import com.btech.propertymgt.demo.services.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @GetMapping("/properties")
    public ResponseEntity<List<PropertyMarketplaceResponseDto>> getAvailableMarketplaceListings() {
        return ResponseEntity.ok(marketplaceService.getAvailableListings());
    }

    @GetMapping("/properties/{id}")
    public ResponseEntity<PropertyMarketplaceResponseDto> getListingDetails(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(marketplaceService.getListingDetails(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
