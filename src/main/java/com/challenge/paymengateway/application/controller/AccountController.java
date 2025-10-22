package com.challenge.paymengateway.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.paymengateway.application.dto.AccountResponseDTO;
import com.challenge.paymengateway.application.dto.DepositRequestDTO;
import com.challenge.paymengateway.application.dto.DepositResponseDTO;
import com.challenge.paymengateway.application.service.AccountService;
import com.challenge.paymengateway.config.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/accounts")
public class AccountController {
  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @GetMapping("/")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<AccountResponseDTO> getAccountByUser(@AuthenticationPrincipal UserDetailsImpl user) {
    AccountResponseDTO responseDTO = accountService.getAccountByUserId(user.getId());
    return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
  }

  @PostMapping("/deposit")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<DepositResponseDTO> deposit(@AuthenticationPrincipal UserDetailsImpl user, @Valid @RequestBody DepositRequestDTO dto) {
    DepositResponseDTO response = accountService.deposit(user.getId(), dto);
    return ResponseEntity.ok(response);
  }
  
}
