package com.rakshit.razorpay.operations.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class WebhookClientConfig {

    @Bean
    public RestClient webhookRestClient(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // if connection not establishes within 3 sec then timeout
        factory.setReadTimeout(5000); //wait time for data. if response is not returned within 5 sec then timeout.

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }

}
