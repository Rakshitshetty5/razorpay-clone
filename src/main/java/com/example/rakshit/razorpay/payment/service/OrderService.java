package com.example.rakshit.razorpay.payment.service;

import com.example.rakshit.razorpay.payment.dto.request.CreateOrderRequest;
import com.example.rakshit.razorpay.payment.dto.response.OrderResponse;
import com.example.rakshit.razorpay.payment.entity.OrderRecord;

import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
}
