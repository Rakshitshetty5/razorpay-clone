package com.example.rakshit.razorpay.payment.service.impl;

import com.example.rakshit.razorpay.common.enums.OrderStatus;
import com.example.rakshit.razorpay.common.exception.BusinessRuleViolationException;
import com.example.rakshit.razorpay.common.exception.DuplicateResourceException;
import com.example.rakshit.razorpay.common.exception.ResourceNotFoundException;
import com.example.rakshit.razorpay.payment.dto.request.CreateOrderRequest;
import com.example.rakshit.razorpay.payment.dto.response.OrderResponse;
import com.example.rakshit.razorpay.payment.dto.response.PaymentResponse;
import com.example.rakshit.razorpay.payment.entity.OrderRecord;
import com.example.rakshit.razorpay.payment.entity.Payment;
import com.example.rakshit.razorpay.payment.mapper.OrderMapper;
import com.example.rakshit.razorpay.payment.mapper.PaymentMapper;
import com.example.rakshit.razorpay.payment.repository.OrderRepository;
import com.example.rakshit.razorpay.payment.repository.PaymentRespository;
import com.example.rakshit.razorpay.payment.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentRespository paymentRepository;

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
        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId){

        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public OrderResponse cancel(UUID merchantId, UUID orderId){
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if(orderRecord.getOrderStatus() == OrderStatus.CANCELLED || orderRecord.getOrderStatus() == OrderStatus.PAID){
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL", "Cannot cancel order with status: " + orderRecord.getOrderStatus());
        }

        orderRecord.setOrderStatus(OrderStatus.CANCELLED);

        orderRecord = orderRepository.save(orderRecord);

        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId){
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        List<Payment> paymentList = paymentRepository.findByOrder_id(orderRecord);

        return paymentMapper.toResponseList(paymentList);
    }


}
