package com.challenge.paymengateway.application.dto;

import java.math.BigDecimal;

public record DepositResponseDTO(Integer accountId, BigDecimal newBalance) {}
