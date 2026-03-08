package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String receiptNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_transaction_id", nullable = false, updatable = false)
    private PaymentTransaction paymentTransaction;

    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(updatable = false)
    private String issuedBy;

    private Boolean voided = false;

    @Column(columnDefinition = "TEXT")
    private String voidReason;

    private LocalDateTime voidedAt;
}
