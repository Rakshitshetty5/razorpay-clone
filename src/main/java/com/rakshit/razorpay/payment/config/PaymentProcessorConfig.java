package com.rakshit.razorpay.payment.config;

import com.rakshit.razorpay.common.enums.PaymentMethod;
import com.rakshit.razorpay.payment.processor.PaymentProcessor;
import com.rakshit.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.rakshit.razorpay.payment.processor.strategy.NetBankingProcessor;
import com.rakshit.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final NetBankingProcessor netBankingProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;


    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return Map.of(
                PaymentMethod.NETBANKING, netBankingProcessor,
                PaymentMethod.UPI, upiPaymentProcessor,
                PaymentMethod.CARD, cardPaymentProcessor
        );
    }
}
