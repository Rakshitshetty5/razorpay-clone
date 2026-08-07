package com.example.rakshit.razorpay.payment.service.impl;


import com.example.rakshit.razorpay.common.enums.OrderStatus;
import com.example.rakshit.razorpay.common.enums.PaymentEvent;
import com.example.rakshit.razorpay.common.enums.PaymentStatus;
import com.example.rakshit.razorpay.common.exception.BusinessRuleViolationException;
import com.example.rakshit.razorpay.common.exception.ResourceNotFoundException;
import com.example.rakshit.razorpay.payment.dto.request.PaymentInitRequest;
import com.example.rakshit.razorpay.payment.dto.response.PaymentResponse;
import com.example.rakshit.razorpay.payment.entity.OrderRecord;
import com.example.rakshit.razorpay.payment.entity.Payment;
import com.example.rakshit.razorpay.payment.gateway.PaymentGatewayRouter;
import com.example.rakshit.razorpay.payment.gateway.dto.PaymentRequest;
import com.example.rakshit.razorpay.payment.gateway.dto.PaymentResult;
import com.example.rakshit.razorpay.payment.mapper.PaymentMapper;
import com.example.rakshit.razorpay.payment.repository.OrderRepository;
import com.example.rakshit.razorpay.payment.repository.PaymentRespository;
import com.example.rakshit.razorpay.payment.service.PaymentService;
import com.example.rakshit.razorpay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRespository paymentRespository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentTransitionService paymentTransitionService;


    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request){
//        OrderRecord order = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));

        OrderRecord order = orderRepository.findByIdAndMerchantIdForUpdate(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.orderId()));

        if(order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.ATTEMPTED){
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE",
                    "Order cannot accept payment in status: " + order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getAmount())
                .merchantId(merchantId)
                .method(request.method())
                .status(PaymentStatus.CREATED)
                .idempotencyKey(UUID.randomUUID().toString()) //TODO
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRespository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(
                payment.getId(),
                payment.getOrder().getId(),
                merchantId,
                order.getAmount(),
                request.method(),
                request.methodDetails()
        );

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result){
            case PaymentResult.Pending pending -> payment.setProcessorReference(pending.registrationRef());
            case PaymentResult.Failure failure -> {
//                payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
            case PaymentResult.Success success -> {

            }
        }

        payment = paymentRespository.save(payment);
        orderRepository.save(order);

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {
//        Payment payment = paymentRespository.findByIdAndMerchantId(paymentId, merchantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        Payment payment = paymentRespository.findByIdAndMerchantIdForUpdate(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(
                payment.getMethod(),
                paymentId
        );

        if(paymentResult instanceof PaymentResult.Success success){
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment captured, payment id: {}", paymentId);
        }else if(paymentResult instanceof PaymentResult.Failure failure){
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
            log.warn("Payment capture failed, paymentId: {}", paymentId);
        }

        payment = paymentRespository.save(payment);

        //TODO: send kafka event

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve,
                                     String bankRef, String errorCode, String errorDescription) {

//        Payment payment = paymentRespository.findById(paymentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        Payment payment = paymentRespository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        if(payment.getStatus() != PaymentStatus.AUTHORIZING){
            log.warn("Payment is not in Authorizing state, paymentID: {}, status: {}", paymentId, payment.getStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrder();

        if(approve){
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS); //Authorized
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            //AUTO-CAPTURE
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST); //capturing
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);

            if(captureResult instanceof PaymentResult.Success success){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS); //captured
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            }else if(captureResult instanceof PaymentResult.Failure failure){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL); // capture fail
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
            }
        }else{
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRespository.save(payment);
        orderRepository.save(orderRecord);

        //TODO: Send an outbox kafka event

    }

}
