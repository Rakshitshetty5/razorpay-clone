package com.example.rakshit.razorpay.merchant.repository;

import com.example.rakshit.razorpay.merchant.dto.response.ApiKeyResponse;
import com.example.rakshit.razorpay.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKeyResponse> findByMerchant_Id(UUID merchantId);
}
