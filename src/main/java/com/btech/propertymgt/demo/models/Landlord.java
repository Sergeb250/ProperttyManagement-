package com.btech.propertymgt.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "landlords")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Landlord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "landlord", cascade = CascadeType.ALL)
    private List<Property> ownedProperties;

    @Enumerated(EnumType.STRING)
    private PaymentReceiptPreference paymentReceiptPreference;

    private String momoPayNumber;

    private String bankName;

    private String bankAccountNumber;

    public enum PaymentReceiptPreference {
        MOMO_PAY, BANK_ACCOUNT
    }
}
