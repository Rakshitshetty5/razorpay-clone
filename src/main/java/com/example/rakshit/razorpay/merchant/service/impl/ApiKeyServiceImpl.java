package com.example.rakshit.razorpay.merchant.service.impl;

import com.example.rakshit.razorpay.common.exception.ResourceNotFoundException;
import com.example.rakshit.razorpay.common.util.RandomizerUtil;
import com.example.rakshit.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyResponse;
import com.example.rakshit.razorpay.merchant.entity.ApiKey;
import com.example.rakshit.razorpay.merchant.entity.Merchant;
import com.example.rakshit.razorpay.merchant.repository.ApiKeyRepository;
import com.example.rakshit.razorpay.merchant.repository.MerchantRepository;
import com.example.rakshit.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;
    private BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request){

        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        String keyId = "rzp_" + request.environment().name().toUpperCase() + RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .keyId(keyId)
                .keySecretHash(BCRYPT.encode(rawSecret))
                .merchant(merchant)
                .environment(request.environment())
                .build();

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }

    @Override
    public List<ApiKeyResponse> listByMerchant(UUID merchantId){
        return apiKeyRepository.findByMerchant_Id(merchantId).stream()
                .map(apikey -> new ApiKeyResponse(
                        apikey.id(),
                        apikey.keyId(),
                        apikey.environment(),
                        apikey.enabled(),
                        apikey.lastUsedAt(),
                        null
                )).toList();
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId){
        ApiKey key = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));
        key.setEnabled(false);
//        apiKeyRepository.save(key);
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, UUID keyId){
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", keyId));

        if(!apiKey.isEnabled()) throw new RuntimeException("Cannot rotate a disbaled key");

        String newSecretHash = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(BCRYPT.encode(newSecretHash));
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponse(
                apiKey.getId(),
                apiKey.getKeyId(),
                apiKey.getKeySecretHash(),
                apiKey.getEnvironment()
        );
    }

}
