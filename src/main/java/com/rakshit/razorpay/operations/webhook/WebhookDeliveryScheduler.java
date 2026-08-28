package com.rakshit.razorpay.operations.webhook;

import com.rakshit.razorpay.common.enums.WebhookEventStatus;
import com.rakshit.razorpay.operations.entity.WebhookEvent;
import com.rakshit.razorpay.operations.repository.WebhookEventRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookDeliveryScheduler {

    private final WebhookRetryQueue retryQueue;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookDeliveryExecutor deliveryExecutor;

    private ExecutorService virtualThreadExecutor;

    @PostConstruct
    void init(){
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @PreDestroy
    void shutDown(){
        virtualThreadExecutor.shutdown();
    }

    @Value("${app.webhook.delivery.poll-batch-size:100}")
    private int batchSize = 100;


    @Scheduled(fixedDelay = 1000)
    public void pollAndDeliver() {
        Set<UUID> due = retryQueue.pollDue(batchSize);

        if (due.isEmpty()) return;

        for (UUID webhookEventId: due) {
            virtualThreadExecutor.submit(() -> {
                deliveryExecutor.deliver(webhookEventId);
            });

            //each delivery will have on seperate threads parallel.
            // without virtual thread it needs to wait for previous delivery to be completed
            // after all delivery is done only then after a elapse of 1sec next poll will happen
        }
    }


    @Scheduled(fixedDelay = 10000)
    public void reconcileFromDatabase() {
        LocalDateTime now = LocalDateTime.now();
        List<WebhookEvent> due = webhookEventRepository
                .findByStatusAndNextRetryAtBefore(WebhookEventStatus.PENDING, now);

        for (WebhookEvent event: due) {
            retryQueue.enqueueIfAbsent(event.getId(), event.getNextRetryAt());
        }
    }


}
