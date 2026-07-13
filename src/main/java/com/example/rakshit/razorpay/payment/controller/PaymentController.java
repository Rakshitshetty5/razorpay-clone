package com.example.rakshit.razorpay.payment.controller;

import com.example.rakshit.razorpay.payment.dto.request.PaymentInitRequest;
import com.example.rakshit.razorpay.payment.dto.response.PaymentResponse;
import com.example.rakshit.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    UUID merchantId = UUID.fromString("e60239ea-0533-441f-833b-cbd1f71a67de");

    @PostMapping
    public ResponseEntity<PaymentResponse> initate(@RequestBody @Valid PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantId, request));
    }
}
