package com.challenge.paymengateway.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.challenge.paymengateway.application.dto.UserCreateDTO;
import com.challenge.paymengateway.application.dto.UserResponseDTO;
import com.challenge.paymengateway.application.model.Account;
import com.challenge.paymengateway.application.model.User;
import com.challenge.paymengateway.application.repository.AccountRepository;
import com.challenge.paymengateway.application.repository.UserRepository;
import com.challenge.paymengateway.common.utils.CPFUtils;

import jakarta.transaction.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  
  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, AccountRepository accountRepository) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.passwordEncoder = passwordEncoder;
  }
  
  @Transactional
  public UserResponseDTO createUser(UserCreateDTO dto) {
    if (userRepository.existsByEmail(dto.getEmail())) {
        throw new IllegalArgumentException("E-mail já cadastrado");
    }
    if (userRepository.existsByCpf(dto.getCpf())) {
        throw new IllegalArgumentException("CPF já cadastrado");
    }

    User user = new User();
    user.setName(dto.getName());
    user.setEmail(dto.getEmail());

    String stripped_cpf = dto.getCpf().replaceAll("\\D", "");

    if (!CPFUtils.isValidCPF(stripped_cpf)) {
        throw new IllegalArgumentException("CPF inválido");
    }

    user.setCpf(stripped_cpf);
    user.setPassword(passwordEncoder.encode(dto.getPassword()));

    User savedUser = userRepository.save(user);

    Account account = new Account();
    account.setUser(savedUser);
    account.setBalance(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_EVEN));

    accountRepository.save(account);

    return new UserResponseDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getCpf());
  }

  public List<User> listAll() {
    return userRepository.findAll();
  }
}
