package com.example.rakshit.razorpay.vault.service;

import com.example.rakshit.razorpay.common.entity.Money;
import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.example.rakshit.razorpay.vault.dto.request.TokenizeRequest;
import com.example.rakshit.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {

    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);

}
