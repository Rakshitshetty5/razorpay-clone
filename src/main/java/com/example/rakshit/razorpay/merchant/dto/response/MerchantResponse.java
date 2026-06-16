package com.example.rakshit.razorpay.merchant.dto.response;


import com.example.rakshit.razorpay.common.enums.BusinessType;
import com.example.rakshit.razorpay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {


}
