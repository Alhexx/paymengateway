package com.challenge.paymengateway.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.challenge.paymengateway.application.dto.AccountResponseDTO;
import com.challenge.paymengateway.application.dto.DepositRequestDTO;
import com.challenge.paymengateway.application.dto.DepositResponseDTO;
import com.challenge.paymengateway.application.model.Account;
import com.challenge.paymengateway.application.repository.AccountRepository;
import com.challenge.paymengateway.common.components.PaymentValidator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class AccountService {
  private final AccountRepository accountRepository;
  private final PaymentValidator paymentValidator;

  public AccountService(AccountRepository accountRepository, PaymentValidator paymentValidator) {
    this.accountRepository = accountRepository;
    this.paymentValidator = paymentValidator;
  }

  public AccountResponseDTO getAccountByUserId(Integer userId) {
    Account account = accountRepository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada para o usuário ID: " + userId));

    return AccountResponseDTO.from(account);
  }

  @Transactional
  public DepositResponseDTO deposit(Integer userId, DepositRequestDTO dto) {
      Account account = accountRepository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("Conta não encontrada para o usuário ID: " + userId));

      boolean approved = paymentValidator.validateDeposit();
      if (!approved) {
        throw new IllegalArgumentException("Depósito não aprovado pelo autorizador externo");
      }

      BigDecimal newBalance = account.getBalance()
        .add(dto.amount())
        .setScale(4, RoundingMode.HALF_EVEN);

      account.setBalance(newBalance);
      accountRepository.save(account);

      return new DepositResponseDTO(account.getId(), newBalance);
  }
}
