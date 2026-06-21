package com.example.rakshit.razorpay.payment.service.impl;

import com.example.rakshit.razorpay.common.enums.OrderStatus;
import com.example.rakshit.razorpay.common.exception.DuplicateResourceException;
import com.example.rakshit.razorpay.payment.dto.request.CreateOrderRequest;
import com.example.rakshit.razorpay.payment.dto.response.OrderResponse;
import com.example.rakshit.razorpay.payment.entity.OrderRecord;
import com.example.rakshit.razorpay.payment.repository.OrderRepository;
import com.example.rakshit.razorpay.payment.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    public OrderResponse create(UUID merchantId, CreateOrderRequest request){

        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE", "Order with receipt already exists: " + request.receipt());
        }

        OrderRecord orderRecord = OrderRecord.builder()
                .receipt(request.receipt())
                .orderStatus(OrderStatus.CREATED)
                .amount(request.amount())
                .merchantId(merchantId)
                .notes(request.notes())
                .expiresAt(request.expiresAt() != null ? request.expiresAt() :
                        LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();
        orderRecord = orderRepository.save(orderRecord);
        return new OrderResponse(
                orderRecord.getId(),
                orderRecord.getMerchantId(),
                orderRecord.getReceipt(),
                orderRecord.getAmount(),
                orderRecord.getOrderStatus(),
                orderRecord.getAttempts(),
                orderRecord.getNotes(),
                orderRecord.getExpiresAt()
        );
    }

}
