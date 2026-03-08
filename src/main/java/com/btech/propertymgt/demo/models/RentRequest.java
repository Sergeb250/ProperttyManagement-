package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rent_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentRequest {

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
    private Room room; // Nullable if renting the whole property

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private Landlord landlord;

    private LocalDate requestedMoveInDate;

    private BigDecimal proposedRentAmount;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentRequestStatus status = RentRequestStatus.SUBMITTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestSource requestSource = RequestSource.WEB;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    private String reviewedBy;

    @Column(columnDefinition = "TEXT")
    private String approvalNote;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum RentRequestStatus {
        SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, CANCELLED, AGREEMENT_PENDING, PAYMENT_PENDING, COMPLETED
    }

    public enum RequestSource {
        WEB, MOBILE, ADMIN_DASHBOARD, LANDLORD_MANUAL
    }
}
