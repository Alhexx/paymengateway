package com.challenge.paymengateway.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.challenge.paymengateway.application.dto.AccountResponseDTO;
import com.challenge.paymengateway.application.dto.DepositRequestDTO;
import com.challenge.paymengateway.application.dto.DepositResponseDTO;
import com.challenge.paymengateway.application.model.Account;
import com.challenge.paymengateway.application.model.User;
import com.challenge.paymengateway.application.repository.AccountRepository;
import com.challenge.paymengateway.common.components.PaymentValidator;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentValidator paymentValidator;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setId(1);
        user.setName("João Silva");
        user.setEmail("joao@email.com");
        user.setCpf("12345678909");

        account = new Account();
        account.setId(1);
        account.setBalance(BigDecimal.valueOf(100).setScale(4));
        account.setUser(user);
    }

    @Test
    void shouldGetAccountByUserIdSuccessfully() {
        when(accountRepository.findByUserId(1)).thenReturn(Optional.of(account));

        AccountResponseDTO result = accountService.getAccountByUserId(1);

        assertNotNull(result);
        assertEquals(1, result.id());
        assertEquals(account.getBalance(), result.balance());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountRepository.findByUserId(1)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            accountService.getAccountByUserId(1);
        });

        assertEquals("Conta não encontrada para o usuário ID: 1", ex.getMessage());
    }

    @Test
    void shouldDepositSuccessfully() {
        when(accountRepository.findByUserId(1)).thenReturn(Optional.of(account));
        when(paymentValidator.validateOperation()).thenReturn(true);
        DepositRequestDTO dto = new DepositRequestDTO(BigDecimal.valueOf(50));

        DepositResponseDTO result = accountService.deposit(1, dto);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(150).setScale(4), result.newBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldFailDepositWhenNotApproved() {
        when(accountRepository.findByUserId(1)).thenReturn(Optional.of(account));
        when(paymentValidator.validateOperation()).thenReturn(false);
        DepositRequestDTO dto = new DepositRequestDTO(BigDecimal.valueOf(50));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            accountService.deposit(1, dto);
        });

        assertEquals("Depósito não aprovado pelo autorizador externo", ex.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }
}
