package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_request_id", nullable = false)
    private PaymentRequest paymentRequest;

    @Column(nullable = false)
    private BigDecimal amount;

    private String method; // e.g. MOMO, CARD
    private String provider;
    private String externalTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentTransactionStatus status = PaymentTransactionStatus.INITIATED;

    private LocalDateTime paidAt;

    public enum PaymentTransactionStatus {
        INITIATED, PROCESSING, SUCCESS, FAILED, CANCELLED, REFUNDED
    }
}
