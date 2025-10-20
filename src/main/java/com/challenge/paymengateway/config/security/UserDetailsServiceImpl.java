package com.challenge.paymengateway.config.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.challenge.paymengateway.application.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String usernameOrCpf) throws UsernameNotFoundException {
    return userRepository.findByEmail(usernameOrCpf)
      .or(() -> userRepository.findByCpf(usernameOrCpf))
      .map(UserDetailsImpl::new)
      .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
  }
  
}
