package com.example.rakshit.razorpay.payment.processor.strategy;

import com.example.rakshit.razorpay.payment.processor.PaymentProcessor;
import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class UpiPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request){
        return null;
    }

}
