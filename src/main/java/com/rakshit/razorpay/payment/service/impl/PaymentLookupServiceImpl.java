package com.rakshit.razorpay.payment.service.impl;

import com.rakshit.razorpay.common.enums.PaymentStatus;
import com.rakshit.razorpay.payment.api.PaymentLookupService;
import com.rakshit.razorpay.payment.entity.Payment;
import com.rakshit.razorpay.payment.repository.PaymentRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentLookupServiceImpl implements PaymentLookupService {

    private final PaymentRespository paymentRepository;

    @Override
    public List<Payment> findUnsettledCapturedPayments(UUID merchantId) {
        return paymentRepository.findByMerchantIdAndStatusForUpdate(merchantId, PaymentStatus.CAPTURED);
    }
}
