package com.example.rakshit.razorpay.payment.service;

import com.example.rakshit.razorpay.payment.dto.request.PaymentInitRequest;
import com.example.rakshit.razorpay.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse intiate(UUID merchantId, PaymentInitRequest request);
}
