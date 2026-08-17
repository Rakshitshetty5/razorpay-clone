package com.rakshit.razorpay.payment.simulator;


import com.rakshit.razorpay.common.enums.ChaosMode;
import com.rakshit.razorpay.common.enums.PaymentStatus;
import com.rakshit.razorpay.common.util.RandomizerUtil;
import com.rakshit.razorpay.payment.entity.Payment;
import com.rakshit.razorpay.payment.repository.PaymentRespository;
import com.rakshit.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {

    private final PaymentRespository paymentRespository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

//    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallback(){

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidates = paymentRespository
                .findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow);

        log.info("Simulating payments for {} payments", candidates.size());

        if(candidates.isEmpty()) return;

        for (Payment payment: candidates){
            simulateCallback(payment);
        }
    }

    private void simulateCallback(Payment payment){
        SimulatorConfig.MethodSimulatorConfig methodConfig = simulatorConfig.configFor(payment.getMethod());

        LocalDateTime dueAt = dueAt(payment, methodConfig);

        if(LocalDateTime.now().isBefore(dueAt)) return;

        ChaosMode chaosMode = simulatorConfig.getChaosMode();

        switch (chaosMode){
            case SUCCESS -> resolve(payment, true);
            case FAILURE -> resolve(payment, false);
            case TIMEOUT -> log.debug("BankCallback simulator: Payment Timed Out");
            case NORMAL, SLOW -> resolve(payment, shouldApprove(payment, methodConfig));
        }
    }

    public void resolve(Payment payment, boolean approve){
        if(approve){
            String bankRef = "SIM_BANK_REF" + RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankRef, null, null);
        }else{
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank Declined");
        }
    }

    public boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig){
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodSimulatorConfig.getSuccessRate();
    }

    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig){

        int range = methodSimulatorConfig.getMaxDelaySeconds() - methodSimulatorConfig.getMinDelaySeconds();
        //get random delay in range min and max. Everytime delay would be consistent for same payment id because of
        // hash unless config changes. this makes simulator deterministic.
        int delaySeconds = methodSimulatorConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range+1);

        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW){
            delaySeconds *= 2;
        }

        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }



}
