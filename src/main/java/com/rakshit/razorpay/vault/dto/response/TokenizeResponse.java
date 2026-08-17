package com.rakshit.razorpay.vault.dto.response;

import com.rakshit.razorpay.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
