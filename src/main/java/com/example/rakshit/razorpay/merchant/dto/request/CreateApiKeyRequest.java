package com.example.rakshit.razorpay.merchant.dto.request;

import com.example.rakshit.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
