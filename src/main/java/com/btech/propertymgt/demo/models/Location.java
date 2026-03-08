package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Location parentLocation;

    private String fullPath;

    private Double latitude;

    private Double longitude;

    private Boolean active = true;

    @OneToMany(mappedBy = "parentLocation", cascade = CascadeType.ALL)
    private List<Location> subLocations;

    public enum LocationType {
        COUNTRY, PROVINCE, CITY, DISTRICT, SECTOR, CELL, VILLAGE
    }
}
