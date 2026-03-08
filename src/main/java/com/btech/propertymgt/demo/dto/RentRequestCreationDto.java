package com.btech.propertymgt.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RentRequestCreationDto {
    private Long tenantId;
    private Long propertyId;
    private Long roomId; // Optional
    private LocalDate requestedMoveInDate;
    private BigDecimal proposedRentAmount;
    private String message;
    private String requestSource; // e.g. WEB, MOBILE
}
