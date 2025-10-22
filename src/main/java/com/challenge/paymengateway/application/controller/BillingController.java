package com.challenge.paymengateway.application.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.paymengateway.application.dto.BillingRequestDTO;
import com.challenge.paymengateway.application.dto.BillingResponseDTO;
import com.challenge.paymengateway.application.dto.PaymentRequestDTO;
import com.challenge.paymengateway.application.dto.PaymentResponseDTO;
import com.challenge.paymengateway.application.service.BillingService;
import com.challenge.paymengateway.common.enums.StatusCobranca;
import com.challenge.paymengateway.config.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/billings")
public class BillingController {
  private final BillingService billingService;

  public BillingController(BillingService billingService) {
    this.billingService = billingService;
  }

  @PostMapping
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<BillingResponseDTO> postMethodName(@AuthenticationPrincipal UserDetailsImpl user, @Valid @RequestBody BillingRequestDTO dto) {
      BillingResponseDTO billingResponseDTO = billingService.createBilling(user.getId(), dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(billingResponseDTO);
  }

  @GetMapping("/sender")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<List<BillingResponseDTO>> getSendedBills(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam(required = false) StatusCobranca status) {
      List<BillingResponseDTO> billings = billingService.getBillingByUserId(user.getId(), status, true);
      return ResponseEntity.ok(billings);
  }

  @GetMapping("/reciever")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<List<BillingResponseDTO>> getRecievedBills(@AuthenticationPrincipal UserDetailsImpl user, @RequestParam(required = false) StatusCobranca status) {
      List<BillingResponseDTO> billings = billingService.getBillingByUserId(user.getId(), status, false);
      return ResponseEntity.ok(billings);
  }
  
  @PostMapping("/pay")
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<PaymentResponseDTO> postMethodName(@AuthenticationPrincipal UserDetailsImpl user, @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
      PaymentResponseDTO paymentResponseDTO = billingService.processPayment(user.getId(), paymentRequestDTO);

      if ("FAILED".equalsIgnoreCase(paymentResponseDTO.status())) {
          return ResponseEntity
                  .status(HttpStatus.PAYMENT_REQUIRED)
                  .body(paymentResponseDTO);
      }

      return ResponseEntity.ok(paymentResponseDTO);
  }
}
