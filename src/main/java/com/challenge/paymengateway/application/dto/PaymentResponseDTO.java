package com.challenge.paymengateway.application.dto;

import com.challenge.paymengateway.common.enums.PaymentMethods;

public record PaymentResponseDTO(String status, String message, PaymentMethods method) {
  public static PaymentResponseDTO from(String status, String message, PaymentMethods method) {
    return new PaymentResponseDTO(status, message, method);
  }
}
