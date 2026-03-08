package com.btech.propertymgt.demo.dtos.responses;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PropertySetupResponse {
    private Long propertyId;
    private String propertyCode;
    private String title;
    private String status;
    private String listingStatus;
    private int propertyImagesCount;
    private int propertyVideosCount;
    private int roomsCount;
    private int roomImagesCount;
    private boolean completedSetup;
    private boolean published;
    private List<String> warnings;
    private String message;
}
