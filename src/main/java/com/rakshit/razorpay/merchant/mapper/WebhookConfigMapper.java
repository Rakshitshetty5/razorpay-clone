package com.rakshit.razorpay.merchant.mapper;

import com.rakshit.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.rakshit.razorpay.merchant.entity.MerchantWebHookConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebhookConfigMapper {

    @Mapping(target = "webhookSecret", source = "rawSecret")
    WebhookConfigResponse toResponse(MerchantWebHookConfig merchantWebhookConfig, String rawSecret);

}