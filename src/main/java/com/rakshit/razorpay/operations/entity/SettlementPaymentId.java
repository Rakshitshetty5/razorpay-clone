package com.rakshit.razorpay.operations.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SettlementPaymentId implements Serializable {

    private UUID settlementId;

    private UUID paymentId;
}
