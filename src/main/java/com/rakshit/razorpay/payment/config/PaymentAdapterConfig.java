package com.rakshit.razorpay.payment.config;

import com.rakshit.razorpay.common.enums.PaymentMethod;
import com.rakshit.razorpay.payment.gateway.PaymentAdapter;
import com.rakshit.razorpay.payment.gateway.adapters.CardPaymentAdapter;
import com.rakshit.razorpay.payment.gateway.adapters.NetBankingAdapter;
import com.rakshit.razorpay.payment.gateway.adapters.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
        return Map.of(
                PaymentMethod.NETBANKING, netBankingAdapter,
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.UPI, upiPaymentAdapter
        );
    }

}
