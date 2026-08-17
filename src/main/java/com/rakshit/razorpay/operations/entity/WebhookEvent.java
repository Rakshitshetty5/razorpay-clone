package com.rakshit.razorpay.operations.entity;

import com.rakshit.razorpay.common.entity.BaseEntity;
import com.rakshit.razorpay.common.enums.WebhookEventStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "webhook_event")
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class WebhookEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String signature;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(nullable = false)
    private String targetUrl;

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
