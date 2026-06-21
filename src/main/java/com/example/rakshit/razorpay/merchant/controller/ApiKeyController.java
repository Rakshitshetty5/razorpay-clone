package com.example.rakshit.razorpay.merchant.controller;

import com.example.rakshit.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyResponse;
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

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@PathVariable UUID merchantId, @RequestBody @Valid CreateApiKeyRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                apiKeyService.create(merchantId, request)
        );
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listByMerchant(@PathVariable UUID merchantId){
        return  ResponseEntity.status(HttpStatus.OK).body(apiKeyService.listByMerchant(merchantId));
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID merchantId, @PathVariable UUID keyId){
        apiKeyService.revoke(merchantId, keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotate(@PathVariable UUID merchantId, @PathVariable UUID keyId){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.rotate(merchantId, keyId));
    }

}
