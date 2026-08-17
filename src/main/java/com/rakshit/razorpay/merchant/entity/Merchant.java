package com.rakshit.razorpay.merchant.entity;

import com.rakshit.razorpay.common.entity.BaseEntity;
import com.rakshit.razorpay.common.enums.BusinessType;
import com.rakshit.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "merchant",
    indexes = {
        @Index(
                name = "idx_merchant_status", columnList = "status"
        )
    }
)
public class Merchant extends BaseEntity {
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
