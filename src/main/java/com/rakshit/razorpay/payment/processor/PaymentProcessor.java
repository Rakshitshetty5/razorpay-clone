package com.rakshit.razorpay.payment.processor;

import com.rakshit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rakshit.razorpay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
