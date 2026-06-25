package com.example.rakshit.razorpay.payment.config;

import com.example.rakshit.razorpay.common.enums.PaymentMethod;
import com.example.rakshit.razorpay.payment.gateway.PaymentAdapter;
import com.example.rakshit.razorpay.payment.gateway.adapters.CardPaymentAdapter;
import com.example.rakshit.razorpay.payment.gateway.adapters.NetBankingAdapter;
import com.example.rakshit.razorpay.payment.gateway.adapters.UpiPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
        return Map.of(
                PaymentMethod.NETBANKING, new NetBankingAdapter(),
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.UPI, new UpiPaymentAdapter()
        );
    }

}
