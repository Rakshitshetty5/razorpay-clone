package com.example.rakshit.razorpay.merchant.service;

import com.example.rakshit.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.example.rakshit.razorpay.merchant.dto.response.MerchantResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
}
