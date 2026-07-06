package com.example.rakshit.razorpay.payment.gateway.adapters;

import com.example.rakshit.razorpay.common.enums.PaymentMethod;
import com.example.rakshit.razorpay.payment.gateway.PaymentAdapter;
import com.example.rakshit.razorpay.payment.gateway.dto.PaymentRequest;
import com.example.rakshit.razorpay.payment.gateway.dto.PaymentResult;
import com.example.rakshit.razorpay.payment.processor.PaymentProcessorRouter;
import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.example.rakshit.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpiPaymentAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest request){
        log.info("Initiate Payment with UPI, paymentId: {}", request.paymentId());

        try{

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                    request.paymentId(),
                    PaymentMethod.UPI,
                    request.amount(),
                    request.methodDetails()
            );

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);

            return switch (paymentProcessorResponse){
                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
                case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());
            };

        }catch (Exception e){
            log.warn("UPI failed, paymentId: {}", request.paymentId());
            return new PaymentResult.Failure("UPI_FAILED", e.getMessage());
        }


    }
}
