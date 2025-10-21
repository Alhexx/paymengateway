package com.challenge.paymengateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient paymentValidatorWebClient(WebClient.Builder builder, @Value("${external.payment-validator.url}") String baseUrl) {
      return builder.baseUrl(baseUrl).build();
    }
}
