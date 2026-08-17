package com.rakshit.razorpay.merchant.service;

import com.rakshit.razorpay.merchant.dto.request.LoginRequest;
import com.rakshit.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.rakshit.razorpay.merchant.dto.response.LoginResponse;
import com.rakshit.razorpay.merchant.dto.response.MerchantResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    LoginResponse login(LoginRequest request);
}
