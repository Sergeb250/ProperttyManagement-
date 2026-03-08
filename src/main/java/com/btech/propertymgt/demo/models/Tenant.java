package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "tenants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<Tenancy> tenancies;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<Agreement> agreements;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private List<PaymentRequest> paymentRequests;

    @Enumerated(EnumType.STRING)
    private PaymentMethod preferredPaymentMethod;

    private String momoPayNumber;

    private String cardNumber; // Note: In production, consider PCI compliance / tokenization

    public enum PaymentMethod {
        MOMO_PAY, CREDIT_CARD
    }
}
