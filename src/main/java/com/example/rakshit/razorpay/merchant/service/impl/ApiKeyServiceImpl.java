package com.example.rakshit.razorpay.merchant.service.impl;

import com.example.rakshit.razorpay.common.exception.ResourceNotFoundException;
import com.example.rakshit.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.example.rakshit.razorpay.merchant.entity.ApiKey;
import com.example.rakshit.razorpay.merchant.entity.Merchant;
import com.example.rakshit.razorpay.merchant.repository.ApiKeyRepository;
import com.example.rakshit.razorpay.merchant.repository.MerchantRepository;
import com.example.rakshit.razorpay.merchant.service.ApiKeyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request){

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        String keyId = "rzp_" + request.environment().name().toUpperCase() + "big_random_sting";
        String rawSecret = "big_random_secret";
        //TODO: random crypto hex

        ApiKey apiKey = ApiKey.builder()
                .keyId(keyId)
                .keySecretHash(rawSecret)
                .merchant(merchant)
                .environment(request.environment())
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }

}
