package com.example.rakshit.razorpay.payment.processor.dto;

import com.example.rakshit.razorpay.common.entity.Money;
import com.example.rakshit.razorpay.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money amount,
        Map<String, Object> methodDetails
) {
}
