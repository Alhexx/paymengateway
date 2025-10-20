package com.challenge.paymengateway.application.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.challenge.paymengateway.application.dto.LoginRequestDTO;
import com.challenge.paymengateway.application.dto.LoginResponseDTO;
import com.challenge.paymengateway.config.security.JwtService;
import com.challenge.paymengateway.config.security.UserDetailsServiceImpl;

@Service
public class AuthService { 
  private final UserDetailsServiceImpl userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder, JwtService jwtService) {
      this.userDetailsService = userDetailsService;
      this.passwordEncoder = passwordEncoder;
      this.jwtService = jwtService;
  }

  public LoginResponseDTO login(LoginRequestDTO dto) {
    UserDetails user = userDetailsService.loadUserByUsername(dto.getEmailOrCpf());

    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Credenciais inválidas");
    }

    String token = jwtService.generateToken(user);

    return new LoginResponseDTO(token);
  }
  
}
