package com.example.rakshit.razorpay.payment.processor;

import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
