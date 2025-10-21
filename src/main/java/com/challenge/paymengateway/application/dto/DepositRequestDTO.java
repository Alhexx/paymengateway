package com.challenge.paymengateway.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DepositRequestDTO(
    @NotNull(message = "O valor do depósito é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor do depósito deve ser maior que zero")
    BigDecimal amount
) {}
