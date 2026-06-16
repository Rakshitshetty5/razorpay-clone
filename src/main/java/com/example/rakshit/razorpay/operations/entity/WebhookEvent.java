package com.example.rakshit.razorpay.operations.entity;

import com.example.rakshit.razorpay.common.enums.WebhookEventStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "webhook_event")
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private String event_type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(nullable = false)
    private String target_url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private WebhookEventStatus status;

    @Column(nullable = false)
    private Integer attempts;

    @Column(nullable = false)
    private Integer lastResponseCode;

    private LocalDateTime nextRetryAt;

    private LocalDateTime lastRetryAt;

    private LocalDateTime createdAt;

    private LocalDateTime deliveredAt;
}
