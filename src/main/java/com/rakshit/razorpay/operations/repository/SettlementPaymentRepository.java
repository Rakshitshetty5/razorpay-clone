package com.rakshit.razorpay.operations.repository;

import com.rakshit.razorpay.operations.entity.SettlementPayment;
import com.rakshit.razorpay.operations.entity.SettlementPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
}
