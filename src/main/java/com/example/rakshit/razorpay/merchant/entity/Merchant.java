package com.example.rakshit.razorpay.merchant.entity;

import com.example.rakshit.razorpay.common.enums.BusinessType;
import com.example.rakshit.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "merchant")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 20)
    private String contactNumber;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 200)
    private String websiteUrl;

    @Column(length = 100)
    private String businessName;

    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(unique = true, length = 20)
    private String gstId;

    @Column(unique = true, length = 20)
    private String panId;

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 200)
    private String settlementIfsc;

    @Column(length = 200)
    private String settlementAccountHolderName;

    @Column(length = 200, nullable = false)
    @Enumerated(EnumType.STRING)
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

}
