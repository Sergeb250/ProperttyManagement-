package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.Landlord;
import com.btech.propertymgt.demo.repositories.LandlordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LandlordService {

    private final LandlordRepository landlordRepository;

    public Landlord createLandlord(Landlord landlord) {
        return landlordRepository.save(landlord);
    }

    public List<Landlord> getAllLandlords() {
        return landlordRepository.findAll();
    }

    public Optional<Landlord> getLandlordById(Long id) {
        return landlordRepository.findById(id);
    }

    @Transactional
    public Landlord updateLandlord(Long id, Landlord landlordDetails) {
        return landlordRepository.findById(id).map(landlord -> {
            landlord.setPaymentReceiptPreference(landlordDetails.getPaymentReceiptPreference());
            landlord.setMomoPayNumber(landlordDetails.getMomoPayNumber());
            landlord.setBankName(landlordDetails.getBankName());
            landlord.setBankAccountNumber(landlordDetails.getBankAccountNumber());

            // Propagate profile details if provided
            if (landlordDetails.getUser() != null) {
                com.btech.propertymgt.demo.models.User existingUser = landlord.getUser();
                com.btech.propertymgt.demo.models.User incomingUser = landlordDetails.getUser();

                if (incomingUser.getFirstName() != null)
                    existingUser.setFirstName(incomingUser.getFirstName());
                if (incomingUser.getLastName() != null)
                    existingUser.setLastName(incomingUser.getLastName());
                if (incomingUser.getPhoneNumber() != null)
                    existingUser.setPhoneNumber(incomingUser.getPhoneNumber());
                if (incomingUser.getProfileImageUrl() != null)
                    existingUser.setProfileImageUrl(incomingUser.getProfileImageUrl());
            }

            return landlordRepository.save(landlord);
        }).orElseThrow(() -> new RuntimeException("Landlord not found with id " + id));
    }

    public void deleteLandlord(Long id) {
        landlordRepository.deleteById(id);
    }
}
