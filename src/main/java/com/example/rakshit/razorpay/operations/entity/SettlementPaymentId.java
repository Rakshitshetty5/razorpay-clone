package com.example.rakshit.razorpay.operations.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class SettlementPaymentId implements Serializable {

    private UUID settlementId;

    private UUID paymentId;
}
