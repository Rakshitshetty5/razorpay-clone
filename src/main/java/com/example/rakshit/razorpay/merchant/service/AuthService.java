package com.example.rakshit.razorpay.merchant.service;

import com.example.rakshit.razorpay.merchant.dto.request.LoginRequest;
import com.example.rakshit.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.example.rakshit.razorpay.merchant.dto.response.LoginResponse;
import com.example.rakshit.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(LoginRequest request);
}
