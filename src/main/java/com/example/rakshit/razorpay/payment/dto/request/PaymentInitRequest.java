package com.example.rakshit.razorpay.payment.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PaymentInitRequest(

        @NotNull(message = "Order Id id required")
        UUID orderId,

        @NotNull(message = "Payment method is required")
        UUID paymentMethod,

        Map<String, Object> methodDetails
) {
}
