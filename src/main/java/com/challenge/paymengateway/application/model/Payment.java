package com.challenge.paymengateway.application.model;

import com.challenge.paymengateway.common.enums.PaymentMethods;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PAYMENTS")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne(optional = false)
  @JoinColumn(name = "billing_id", referencedColumnName = "id")
  private Billing billing;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false)
  private PaymentMethods paymentMethod;

  @Column(nullable = false)
  private Boolean success = false;

  @Column(nullable = false)
  private Boolean cancelled = false;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Billing getBilling() {
    return billing;
  }

  public void setBilling(Billing billing) {
    this.billing = billing;
  }

  public PaymentMethods getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(PaymentMethods paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public Boolean getCancelled() {
    return cancelled;
  }

  public void setCancelled(Boolean cancelled) {
    this.cancelled = cancelled;
  }
}
