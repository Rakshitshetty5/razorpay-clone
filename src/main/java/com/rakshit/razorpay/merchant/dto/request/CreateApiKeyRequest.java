package com.rakshit.razorpay.merchant.dto.request;

import com.rakshit.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
