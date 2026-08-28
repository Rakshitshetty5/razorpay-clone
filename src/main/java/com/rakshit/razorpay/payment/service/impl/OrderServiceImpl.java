package com.rakshit.razorpay.payment.service.impl;

import com.rakshit.razorpay.common.enums.EventAggregateType;
import com.rakshit.razorpay.common.enums.OrderStatus;
import com.rakshit.razorpay.common.exception.BusinessRuleViolationException;
import com.rakshit.razorpay.common.exception.DuplicateResourceException;
import com.rakshit.razorpay.common.exception.ResourceNotFoundException;
import com.rakshit.razorpay.merchant.service.CustomerService;
import com.rakshit.razorpay.payment.dto.request.CreateOrderRequest;
import com.rakshit.razorpay.payment.dto.response.OrderResponse;
import com.rakshit.razorpay.payment.dto.response.PaymentResponse;
import com.rakshit.razorpay.payment.entity.OrderRecord;
import com.rakshit.razorpay.payment.entity.Payment;
import com.rakshit.razorpay.payment.mapper.OrderMapper;
import com.rakshit.razorpay.payment.mapper.PaymentMapper;
import com.rakshit.razorpay.payment.outbox.OutboxEventPublisher;
import com.rakshit.razorpay.payment.repository.OrderRepository;
import com.rakshit.razorpay.payment.repository.PaymentRespository;
import com.rakshit.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentRespository paymentRepository;
    private final CustomerService customerService;
    private final OutboxEventPublisher eventPublisher;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    @Transactional
    public OrderResponse create(UUID merchantId, CreateOrderRequest request){

        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE", "Order with receipt already exists: " + request.receipt());
        }

        UUID customerId = null;
        if(request.customer() != null){
            customerId = customerService.findOrCreate(merchantId,
                    request.customer().email(),
                    request.customer().name(),
                    request.customer().phone());
        }


        OrderRecord orderRecord = OrderRecord.builder()
                .receipt(request.receipt())
                .orderStatus(OrderStatus.CREATED)
                .amount(request.amount())
                .merchantId(merchantId)
                .notes(request.notes())
                .customerId(customerId)
                .expiresAt(request.expiresAt() != null ? request.expiresAt() :
                        LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();
        orderRecord = orderRepository.save(orderRecord);
        eventPublisher.publish(EventAggregateType.ORDER, orderRecord.getId(), "ORDER_CREATED",
                Map.of("orderId", orderRecord.getId(),
                        "merchantId", merchantId.toString(),
                        "orderStatus", orderRecord.getOrderStatus().name(),
                        "amountUnits", orderRecord.getAmount().getAmountUnits(),
                        "amountCurrency", orderRecord.getAmount().getCurrency()
                )
        );
        return orderMapper.toResponse(orderRecord);
    }

    @Override
    public OrderResponse getById(UUID merchantId, UUID orderId){

        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        return orderMapper.toResponse(orderRecord);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID merchantId, UUID orderId){
        OrderRecord orderRecord = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if(orderRecord.getOrderStatus() == OrderStatus.CANCELLED || orderRecord.getOrderStatus() == OrderStatus.PAID){
            throw new BusinessRuleViolationException("ORDER_CANNOT_CANCEL", "Cannot cancel order with status: " + orderRecord.getOrderStatus());
        }

        orderRecord.setOrderStatus(OrderStatus.CANCELLED);

        orderRecord = orderRepository.save(orderRecord);

        eventPublisher.publish(EventAggregateType.ORDER, orderRecord.getId(), "ORDER_CANCELLED",
                Map.of("orderId", orderRecord.getId(),
                        "merchantId", merchantId.toString(),
                        "orderStatus", orderRecord.getOrderStatus().name(),
                        "amountUnits", orderRecord.getAmount().getAmountUnits(),
                        "amountCurrency", orderRecord.getAmount().getCurrency()
                )
        );

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
