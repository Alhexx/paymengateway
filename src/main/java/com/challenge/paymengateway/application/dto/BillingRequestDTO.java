package com.challenge.paymengateway.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record BillingRequestDTO(
    @NotNull(message = "O valor da cobrança é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor da cobrança deve ser maior que zero")
    BigDecimal value,

    @NotNull(message = "O CPF do destinatário é obrigatório")
    String recieverCPF,

    @NotNull(message = "Descrição breve da cobrança é obrigatória")
    String description
) {
  
}
