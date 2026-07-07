package com.example.rakshit.razorpay.payment.gateway;

import com.example.rakshit.razorpay.payment.gateway.dto.PaymentRequest;
import com.example.rakshit.razorpay.payment.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {

    PaymentResult initiate(PaymentRequest request);

    PaymentResult capture(UUID paymentId);
}
