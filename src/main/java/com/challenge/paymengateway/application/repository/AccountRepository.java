package com.challenge.paymengateway.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.paymengateway.application.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
  Optional<Account> findByUserId(Integer userId);
}
