package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_visit_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyVisitRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requesterName;
    private String requesterPhone;
    private String requesterEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant; // Nullable for marketplace guests

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private Landlord landlord;

    private LocalDate preferredVisitDate;
    private String preferredTimeSlot;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitRequestStatus visitStatus = VisitRequestStatus.SUBMITTED;

    private LocalDateTime scheduledVisitDateTime;

    @Column(columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum VisitRequestStatus {
        SUBMITTED, PENDING_RESPONSE, APPROVED, RESCHEDULED, COMPLETED, CANCELLED, REJECTED, NO_SHOW
    }
}
