package com.rakshit.razorpay.operations.settlement;

import com.rakshit.razorpay.common.entity.Money;
import com.rakshit.razorpay.operations.dto.BankTransferResult;

import java.util.UUID;

public interface BankTransferProcessor {
    BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount,
                                String bankAccount, String ifsc);
}
