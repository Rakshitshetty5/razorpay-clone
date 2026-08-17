package com.rakshit.razorpay.payment.controller;

import com.rakshit.razorpay.merchant.security.MerchantContext;
import com.rakshit.razorpay.payment.dto.request.PaymentInitRequest;
import com.rakshit.razorpay.payment.dto.response.PaymentResponse;
import com.rakshit.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<PaymentResponse> initate(@RequestBody @Valid PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantContext.getMerchantId(), request));
    }
}
