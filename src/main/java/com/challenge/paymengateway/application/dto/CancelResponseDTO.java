package com.challenge.paymengateway.application.dto;

import com.challenge.paymengateway.common.enums.StatusCobranca;

public record CancelResponseDTO(StatusCobranca status, String message, Integer billingId) {
  public static CancelResponseDTO from(StatusCobranca status, String message, Integer billingId) {
    return new CancelResponseDTO(status, message, billingId);
  }
}
