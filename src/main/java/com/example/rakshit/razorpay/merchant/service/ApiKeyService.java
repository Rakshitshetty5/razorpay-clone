package com.example.rakshit.razorpay.merchant.service;

import com.example.rakshit.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyCreateResponse;

import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request);
}
