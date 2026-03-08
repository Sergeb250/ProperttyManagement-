package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.dtos.requests.PropertySetupRequest;
import com.btech.propertymgt.demo.dtos.responses.PropertySetupResponse;
import com.btech.propertymgt.demo.models.*;
import com.btech.propertymgt.demo.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertySetupService {

    private final PropertyRepository propertyRepository;
    private final RoomRepository roomRepository;
    private final LandlordRepository landlordRepository;
    private final LocationRepository locationRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public PropertySetupResponse createFullPropertySetup(PropertySetupRequest request,
            MultipartHttpServletRequest multipartRequest) {
        List<String> warnings = new ArrayList<>();

        // 1. Fetch relations
        Landlord landlord = landlordRepository.findById(request.getProperty().getLandlordId())
                .orElseThrow(() -> new RuntimeException("Landlord not found"));

        Location location = null;
        if (request.getProperty().getLocationId() != null) {
            location = locationRepository.findById(request.getProperty().getLocationId()).orElse(null);
        }

        // 2. Validate basics
        if (request.getProperty().getPropertyCode() == null || request.getProperty().getTitle() == null) {
            throw new IllegalArgumentException("Property Code and Title are required.");
        }

        // 3. Create Property
        Property property = new Property();
        property.setLandlord(landlord);
        property.setLocation(location);
        property.setPropertyCode(request.getProperty().getPropertyCode());
        property.setTitle(request.getProperty().getTitle());
        property.setDescription(request.getProperty().getDescription());
        property.setPrice(request.getProperty().getPrice());
        property.setCurrency(request.getProperty().getCurrency() != null ? request.getProperty().getCurrency() : "RWF");
        property.setAddress(request.getProperty().getAddress());
        property.setType(request.getProperty().getType());
        property.setRentalMode(request.getProperty().getRentalMode());
        property.setLatitude(request.getProperty().getLatitude());
        property.setLongitude(request.getProperty().getLongitude());
        property.setAllowVisitRequests(
                request.getProperty().getAllowVisitRequests() != null ? request.getProperty().getAllowVisitRequests()
                        : true);
        property.setAllowRentRequests(
                request.getProperty().getAllowRentRequests() != null ? request.getProperty().getAllowRentRequests()
                        : true);

        // Save base property first to get ID for child references
        property.setStatus(Property.PropertyStatus.AVAILABLE);
        property = propertyRepository.save(property);

        int propImagesCount = 0;
        // 4. Map Property Images
        if (request.getPropertyImagesMeta() != null) {
            for (PropertySetupRequest.PropertyImageMetaRequest meta : request.getPropertyImagesMeta()) {
                MultipartFile file = multipartRequest.getFile(meta.getFileKey());
                if (file != null && !file.isEmpty()) {
                    fileStorageService.uploadPropertyImage(property, file, meta.getCategory(), meta.getCoverImage());
                    propImagesCount++;
                } else {
                    warnings.add("Missing uploaded file for property key: " + meta.getFileKey());
                }
            }
        }

        int roomsCount = 0;
        int roomImagesCount = 0;
        // 5. Process Rooms
        if (request.getRooms() != null) {
            for (PropertySetupRequest.RoomCreatePart roomReq : request.getRooms()) {
                Room room = new Room();
                room.setProperty(property);
                room.setRoomCode(roomReq.getRoomCode());
                room.setRoomName(roomReq.getRoomName());
                room.setDescription(roomReq.getDescription());
                room.setRoomType(roomReq.getRoomType());
                room.setRoomStatus(
                        roomReq.getRoomStatus() != null ? roomReq.getRoomStatus() : Room.RoomStatus.AVAILABLE);
                room.setPrice(roomReq.getPrice());
                room.setSizeSqm(roomReq.getSizeSqm());
                room.setFloorLevel(roomReq.getFloorLevel());
                room.setFurnished(roomReq.getFurnished() != null ? roomReq.getFurnished() : false);
                room.setPrivateBathroom(roomReq.getPrivateBathroom() != null ? roomReq.getPrivateBathroom() : false);
                room.setBalcony(roomReq.getBalcony() != null ? roomReq.getBalcony() : false);
                room.setMaxOccupants(roomReq.getMaxOccupants() != null ? roomReq.getMaxOccupants() : 1);

                room = roomRepository.save(room);
                roomsCount++;

                // Process Room Images
                if (roomReq.getImagesMeta() != null) {
                    for (PropertySetupRequest.RoomImageMetaRequest meta : roomReq.getImagesMeta()) {
                        MultipartFile file = multipartRequest.getFile(meta.getFileKey());
                        if (file != null && !file.isEmpty()) {
                            fileStorageService.uploadRoomImage(room, file, meta.getTitle(), meta.getCoverImage());
                            roomImagesCount++;
                        } else {
                            warnings.add("Missing uploaded file for room key: " + meta.getFileKey());
                        }
                    }
                }
            }
        }

        // 6. Validation & Readiness Rules
        boolean isValid = true;
        if (propImagesCount == 0) {
            warnings.add("Setup incomplete: At least one property image is required.");
            isValid = false;
        }

        if (property.getRentalMode() == Property.RentalMode.BY_ROOMS) {
            property.setRequiresRoomSetup(true);
            if (roomsCount == 0) {
                warnings.add("Setup incomplete: BY_ROOMS property requires at least one room.");
                isValid = false;
            } else if (roomImagesCount == 0) {
                warnings.add("Setup incomplete: BY_ROOMS property requires room images.");
                isValid = false;
            }
        }

        property.setCompletedSetup(isValid);

        // 7. Handle Listing Status based on requesting readiness
        if (request.getProperty().getListingStatus() == Property.ListingStatus.PUBLISHED) {
            if (isValid) {
                property.setListingStatus(Property.ListingStatus.PUBLISHED);
                property.setPublished(true);
            } else {
                warnings.add("Failed to publish property due to setup readiness rules. Saved as DRAFT.");
                property.setListingStatus(Property.ListingStatus.DRAFT);
                property.setPublished(false);
            }
        } else {
            property.setListingStatus(Property.ListingStatus.DRAFT);
            property.setPublished(false);
        }

        propertyRepository.save(property);

        // 8. Return comprehensive payload
        return PropertySetupResponse.builder()
                .propertyId(property.getId())
                .propertyCode(property.getPropertyCode())
                .title(property.getTitle())
                .status(property.getStatus().name())
                .listingStatus(property.getListingStatus().name())
                .propertyImagesCount(propImagesCount)
                .roomsCount(roomsCount)
                .roomImagesCount(roomImagesCount)
                .completedSetup(property.getCompletedSetup())
                .published(property.getPublished())
                .warnings(warnings)
                .message("Property setup processed successfully.")
                .build();
    }
}
