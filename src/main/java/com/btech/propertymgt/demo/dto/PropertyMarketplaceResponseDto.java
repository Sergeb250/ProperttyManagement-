package com.btech.propertymgt.demo.dto;

import com.btech.propertymgt.demo.models.Property;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PropertyMarketplaceResponseDto {
    private Long id;
    private String propertyCode;
    private String title;
    private String description;
    private String type;
    private BigDecimal price;
    private String currency;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer totalRooms;
    private Integer totalBathrooms;
    private Double areaSqm;
    private List<String> coverImageUrls;
    private List<String> amenities;

    // Helper method to convert an entity to DTO easily
    public static PropertyMarketplaceResponseDto fromEntity(Property property) {
        PropertyMarketplaceResponseDto dto = new PropertyMarketplaceResponseDto();
        dto.setId(property.getId());
        dto.setPropertyCode(property.getPropertyCode());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setType(property.getType().name());
        dto.setPrice(property.getPrice());
        dto.setCurrency(property.getCurrency());
        dto.setAddress(property.getAddress());
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        dto.setTotalRooms(property.getTotalRooms());
        dto.setTotalBathrooms(property.getTotalBathrooms());
        dto.setAreaSqm(property.getAreaSqm());

        if (property.getImages() != null) {
            dto.setCoverImageUrls(property.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getCoverImage()))
                    .map(img -> img.getImageUrl())
                    .collect(Collectors.toList()));
        }

        if (property.getAmenities() != null) {
            dto.setAmenities(property.getAmenities().stream()
                    .map(amenity -> amenity.getName())
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
