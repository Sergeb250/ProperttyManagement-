package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "amenities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. WIFI, PARKING, FURNISHED

    private String description;

    // Using simple ManyToMany mapping for flexibility without join entities
    @ManyToMany(mappedBy = "amenities")
    private List<Property> properties;

    @ManyToMany(mappedBy = "amenities")
    private List<Room> rooms;
}
