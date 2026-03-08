package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String roomCode;

    @Column(nullable = false)
    private String roomName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus roomStatus = RoomStatus.AVAILABLE;

    @Column(nullable = false)
    private BigDecimal price;

    private Double sizeSqm;

    private Integer floorLevel;

    private Boolean furnished = false;

    private Boolean privateBathroom = false;

    private Boolean balcony = false;

    private Integer maxOccupants = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomImage> images;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "room_amenities", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private List<Amenity> amenities;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum RoomType {
        SINGLE_ROOM, DOUBLE_ROOM, SELF_CONTAINED_ROOM, STUDIO, MASTER_BEDROOM, SHARED_ROOM, BEDSITTER, SHOP_SPACE,
        OFFICE_SPACE, HALL, STORE, OTHER
    }

    public enum RoomStatus {
        AVAILABLE, RESERVED, PENDING_APPROVAL, OCCUPIED, UNDER_MAINTENANCE, INACTIVE
    }
}
