package com.rakshit.razorpay.payment.processor.strategy;

import com.rakshit.razorpay.common.util.RandomizerUtil;
import com.rakshit.razorpay.payment.processor.PaymentProcessor;
import com.rakshit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.rakshit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "4000000000000002";
    public static final String PAN_CARD_EXPIRED = "4000000000000069";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request){

        String pan = request.pan();

        if(PAN_CARD_DECLINED.equals(pan)){
            log.warn("Card Declined");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card is declined by bank");
        }

        if(PAN_CARD_EXPIRED.equals(pan)){
            log.warn("Pan card has expired");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card declined by bank");
        }

        String processorRef = "CARD_PROCESSOR_" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
