package com.btech.propertymgt.demo.dto;

import lombok.Data;

@Data
public class PaymentInitiationDto {
    private Long tenantId;
    private Long paymentRequestId;
    private String method; // e.g. MOMO, CARD
    private String provider; // e.g. MTN, VISA
    private String returnUrl;
}
