package com.rakshit.razorpay.payment.dto.response;

import com.rakshit.razorpay.common.entity.Money;
import com.rakshit.razorpay.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID merchantId,
        String receipt,
        Money amount,
        UUID customerId,
        OrderStatus status,
        Integer attempts,
        Map<String, Object> notes,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
}
