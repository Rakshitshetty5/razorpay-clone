package com.example.rakshit.razorpay.payment.processor;


import com.example.rakshit.razorpay.common.enums.PaymentMethod;
import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentProcessorRouter {

    private Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request){
        PaymentProcessor processor = paymentProcessorMap.get(request.method());
        if(processor == null){
            throw new IllegalArgumentException("No payment processor registered for this method: " + request.method());
        }
        return processor.charge(request);
    }

}
