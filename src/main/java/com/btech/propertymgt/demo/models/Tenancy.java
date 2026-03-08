package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenancies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id")
    private Agreement agreement;

    private LocalDate moveInDate;
    private LocalDate moveOutDate;

    private BigDecimal rentAmount;
    private BigDecimal depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenancyStatus status = TenancyStatus.PENDING;

    private Boolean active = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TenancyStatus {
        PENDING, RESERVED, ACTIVE, COMPLETED, TERMINATED, CANCELLED
    }
}
