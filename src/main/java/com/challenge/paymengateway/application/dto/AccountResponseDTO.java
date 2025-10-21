package com.challenge.paymengateway.application.dto;

import java.math.BigDecimal;

import com.challenge.paymengateway.application.model.Account;

public record AccountResponseDTO(Integer id, String userName, String userEmail, String userCpf, BigDecimal balance) {
    public static AccountResponseDTO from(Account account) {
        return new AccountResponseDTO(
            account.getId(),
            account.getUser().getName(),
            account.getUser().getEmail(),
            account.getUser().getCpf(),
            account.getBalance()
        );
    }
}