package com.challenge.paymengateway.common.components;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PaymentValidator {
    private final WebClient webClient;

    public PaymentValidator(WebClient paymentValidatorWebClient) {
      this.webClient = paymentValidatorWebClient;
    }

    public boolean validateDeposit() {
      try {
        ValidatorResponse response = webClient.get()
                .retrieve()
                .bodyToMono(ValidatorResponse.class)
                .block();

        return response != null && response.data().authorized();

      } catch (Exception e) {
        throw new IllegalStateException("Erro ao validar operação no autorizador externo", e);
      }
    }

    private record ValidatorResponse(String status, ValidatorData data) {}
    private record ValidatorData(boolean authorized) {}
}