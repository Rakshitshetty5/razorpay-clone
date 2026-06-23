package com.example.rakshit.razorpay.payment.repository;

import com.example.rakshit.razorpay.payment.entity.OrderRecord;
import com.example.rakshit.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRespository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder_id(OrderRecord orderRecord);
}
