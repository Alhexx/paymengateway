package com.challenge.paymengateway.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.paymengateway.application.dto.AccountResponseDTO;
import com.challenge.paymengateway.application.dto.DepositRequestDTO;
import com.challenge.paymengateway.application.dto.DepositResponseDTO;
import com.challenge.paymengateway.application.service.AccountService;

import jakarta.validation.Valid;


@RestController("/accounts")
public class AccountController {
  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping("/{user_id}")
  public ResponseEntity<AccountResponseDTO> getAccountByUser(@PathVariable(value = "user_id") Integer userId) {
    AccountResponseDTO responseDTO = accountService.getAccountByUserId(userId);
    return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
  }

  @PostMapping("/{user_id}/deposit")
  public ResponseEntity<DepositResponseDTO> deposit(@PathVariable("user_id") Integer userId, @Valid @RequestBody DepositRequestDTO dto) {
    DepositResponseDTO response = accountService.deposit(userId, dto);
    return ResponseEntity.ok(response);
  }
  
}
