package com.example.rakshit.razorpay.merchant.controller;

import com.example.rakshit.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyResponse;
import com.example.rakshit.razorpay.merchant.security.MerchantContext;
import com.example.rakshit.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchant/{merchantId}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@RequestBody @Valid CreateApiKeyRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                apiKeyService.create(merchantContext.getMerchantId(), request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listByMerchant(){
        return  ResponseEntity.status(HttpStatus.OK).body(apiKeyService.listByMerchant(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID keyId){
        apiKeyService.revoke(merchantContext.getMerchantId(), keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotate(@PathVariable UUID keyId){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.rotate(merchantContext.getMerchantId(), keyId));
    }

}
