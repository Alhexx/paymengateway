package com.challenge.paymengateway.application.dto;

import java.math.BigDecimal;

import com.challenge.paymengateway.application.model.Billing;
import com.challenge.paymengateway.common.enums.StatusCobranca;

public record BillingResponseDTO(Integer id, String description, BigDecimal value, StatusCobranca status, String recieverEmail, String recieverCpf,String senderEmail, String senderCpf) {
  public static BillingResponseDTO from(Billing billing) {
        return new BillingResponseDTO(
            billing.getId(),
            billing.getDescription(),
            billing.getValue(),
            billing.getStatus(),
            billing.getReciever().getEmail(),
            billing.getReciever().getCpf(),
            billing.getSender().getEmail(),
            billing.getSender().getCpf()
        );
    }
}
