package com.challenge.paymengateway.application.dto;

import java.time.YearMonth;

import com.challenge.paymengateway.common.enums.PaymentMethods;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDTO(
    @NotNull(message = "O ID da cobrança é obrigatório")
    Integer billingId,

    @NotNull(message = "O método de pagamento é obrigatório")
    PaymentMethods paymentMethod,

    String cardNumber,
    String cvv,
    YearMonth cardExpiryDate

) {

    @AssertTrue(message = "Os dados do cartão são obrigatórios quando o método de pagamento é CARTAO.")
    public boolean isCardDataValid() {
        if (paymentMethod == PaymentMethods.CARTAO) {
            return cardNumber != null && cardNumber.matches("\\d{13,19}")
                && cvv != null && cvv.matches("\\d{3,4}")
                && cardExpiryDate != null && cardExpiryDate.isAfter(YearMonth.now());
        }
        return true;
    }
}
