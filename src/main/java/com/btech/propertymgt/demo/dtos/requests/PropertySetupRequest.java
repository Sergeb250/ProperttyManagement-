package com.btech.propertymgt.demo.dtos.requests;

import com.btech.propertymgt.demo.models.Property;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PropertySetupRequest {
    private PropertyCreatePart property;
    private List<PropertyImageMetaRequest> propertyImagesMeta = new ArrayList<>();
    private List<PropertyVideoMetaRequest> propertyVideosMeta = new ArrayList<>();
    private List<RoomCreatePart> rooms = new ArrayList<>();

    @Data
    public static class PropertyCreatePart {
        private Long landlordId;
        private Long locationId;
        private String propertyCode;
        private String title;
        private String description;
        private BigDecimal price;
        private String currency;
        private String address;
        private Property.PropertyType type;
        private Property.RentalMode rentalMode;
        private Property.PropertyStatus status;
        private Property.ListingStatus listingStatus;
        private Double latitude;
        private Double longitude;
        private Boolean allowVisitRequests;
        private Boolean allowRentRequests;
    }

    @Data
    public static class PropertyImageMetaRequest {
        private String fileKey;
        private String title;
        private String caption;
        private String category;
        private Boolean coverImage;
        private Integer displayOrder;
    }

    @Data
    public static class PropertyVideoMetaRequest {
        private String fileKey;
        private String title;
        private String platform;
        private String externalEmbedId;
    }

    @Data
    public static class RoomCreatePart {
        private String clientRoomKey;
        private String roomCode;
        private String roomName;
        private String description;
        private com.btech.propertymgt.demo.models.Room.RoomType roomType;
        private com.btech.propertymgt.demo.models.Room.RoomStatus roomStatus;
        private BigDecimal price;
        private Double sizeSqm;
        private Integer floorLevel;
        private Boolean furnished;
        private Boolean privateBathroom;
        private Boolean balcony;
        private Integer maxOccupants;
        private Boolean rentable;
        private List<RoomImageMetaRequest> imagesMeta = new ArrayList<>();
    }

    @Data
    public static class RoomImageMetaRequest {
        private String fileKey;
        private String title;
        private String caption;
        private Boolean coverImage;
        private Integer displayOrder;
    }
}
