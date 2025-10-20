package com.challenge.paymengateway.application.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.challenge.paymengateway.application.model.User;

@Repository 
public interface UserRepository extends JpaRepository<User, Integer>{
  Optional<User> findByEmail(String email);

  Optional<User> findByCpf(String cpf);

  boolean existsByEmail(String email);

  boolean existsByCpf(String cpf);
}
