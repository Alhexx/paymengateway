package com.challenge.paymengateway.application.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ACCOUNTS")
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @OneToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal balance = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_EVEN);

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public BigDecimal getBalance() {  
    return balance != null ? balance : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_EVEN);
  }

  public void setBalance(BigDecimal balance) {
    if (balance != null) {
        this.balance = balance.setScale(4, RoundingMode.HALF_EVEN);
    } else {
        this.balance = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_EVEN);
    }
  }
}
