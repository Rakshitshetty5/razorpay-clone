package com.rakshit.razorpay.payment.statemachine;

import com.rakshit.razorpay.common.enums.PaymentActor;
import com.rakshit.razorpay.common.enums.PaymentEvent;
import com.rakshit.razorpay.common.enums.PaymentStatus;
import com.rakshit.razorpay.payment.entity.Payment;
import com.rakshit.razorpay.payment.entity.PaymentTransitionLog;
import com.rakshit.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);
        payment.setStatus(next);

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .fromStatus(payment.getStatus())
                .toStatus(next)
                .event(event)
                .payment(payment)
                .actor(PaymentActor.SYSTEM) //TODO: fetch merchant context to identify actor
                .occurredAt(LocalDateTime.now())
                .build();

        paymentTransitionLogRepository.save(log);
        return next;
    }

}
