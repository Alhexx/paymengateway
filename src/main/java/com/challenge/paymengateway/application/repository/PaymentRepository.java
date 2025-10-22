package com.challenge.paymengateway.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.challenge.paymengateway.application.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
  Optional<Payment> findByBillingId(Integer billingId);
}
