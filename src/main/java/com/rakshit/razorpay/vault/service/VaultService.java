package com.rakshit.razorpay.vault.service;

import com.rakshit.razorpay.common.entity.Money;
import com.rakshit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.rakshit.razorpay.vault.dto.request.TokenizeRequest;
import com.rakshit.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {

    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);

}
