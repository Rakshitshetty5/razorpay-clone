package com.example.rakshit.razorpay.payment.repository;

import com.example.rakshit.razorpay.common.enums.PaymentStatus;
import com.example.rakshit.razorpay.payment.entity.OrderRecord;
import com.example.rakshit.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRespository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder_id(OrderRecord orderRecord);

    Optional<Payment> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime globalWindow);
}
