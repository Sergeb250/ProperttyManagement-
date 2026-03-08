package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String propertyCode;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus listingStatus = ListingStatus.DRAFT;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal depositAmount;

    private BigDecimal serviceCharge;

    private String currency = "RWF";

    private String address;

    private Double latitude;

    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private Landlord landlord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private LocalDate availableFrom;

    private Integer totalRooms;

    private Integer totalBathrooms;

    private Integer totalFloors;

    private Double areaSqm;

    private Boolean published = false;

    private Boolean verified = false;

    private Integer viewCount = 0;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Room> rooms;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<PropertyImage> images;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<Tenancy> tenancies;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "property_amenities", joinColumns = @JoinColumn(name = "property_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private List<Amenity> amenities;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum PropertyType {
        ROOM, LODGE, APARTMENT, HOUSE, COMMERCIAL
    }

    public enum PropertyStatus {
        AVAILABLE, PARTIALLY_OCCUPIED, RESERVED, OCCUPIED, UNDER_MAINTENANCE, INACTIVE
    }

    public enum ListingStatus {
        DRAFT, PUBLISHED, UNPUBLISHED, ARCHIVED
    }
}
