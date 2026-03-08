package com.btech.propertymgt.demo.controllers;

import com.btech.propertymgt.demo.models.Landlord;
import com.btech.propertymgt.demo.services.LandlordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/landlords")
@RequiredArgsConstructor
public class LandlordController {

    private final LandlordService landlordService;

    @PostMapping
    public ResponseEntity<Landlord> createLandlord(@RequestBody Landlord landlord) {
        return ResponseEntity.ok(landlordService.createLandlord(landlord));
    }

    @GetMapping
    public ResponseEntity<List<Landlord>> getAllLandlords() {
        return ResponseEntity.ok(landlordService.getAllLandlords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Landlord> getLandlordById(@PathVariable Long id) {
        return landlordService.getLandlordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Landlord> updateLandlord(@PathVariable Long id, @RequestBody Landlord landlordDetails) {
        try {
            return ResponseEntity.ok(landlordService.updateLandlord(id, landlordDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLandlord(@PathVariable Long id) {
        landlordService.deleteLandlord(id);
        return ResponseEntity.ok().build();
    }
}
