package com.challenge.paymengateway.application.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.challenge.paymengateway.common.enums.StatusCobranca;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity 
@Table(name = "BILLINGS")
public class Billing {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "sender_id", referencedColumnName = "id" ,nullable = false)
  private User sender;

  @ManyToOne
  @JoinColumn(name = "receiver_id", referencedColumnName = "id" , nullable = false)
  private User receiver;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal value;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatusCobranca status = StatusCobranca.PENDENTE;

  @Column(length = 255)
  private String description;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public User getReceiver() {
    return receiver;
  }

  public void setReceiver(User receiver) {
    this.receiver = receiver;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public StatusCobranca getStatus() {
    return status;
  }

  public void setStatus(StatusCobranca status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  
}
