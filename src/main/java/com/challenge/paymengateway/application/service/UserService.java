package com.challenge.paymengateway.application.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.challenge.paymengateway.application.dto.UserCreateDTO;
import com.challenge.paymengateway.application.dto.UserResponseDTO;
import com.challenge.paymengateway.application.model.User;
import com.challenge.paymengateway.application.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  
  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
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
    user.setCpf(dto.getCpf());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));

    User saved = userRepository.save(user);
    return new UserResponseDTO(saved.getId(), saved.getName(), saved.getEmail(), saved.getCpf());
  }

  public List<User> listAll() {
    return userRepository.findAll();
  }
}
