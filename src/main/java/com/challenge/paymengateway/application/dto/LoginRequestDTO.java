package com.challenge.paymengateway.application.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "Email ou CPF é obrigatório")
    private String emailOrCpf;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    public String getEmailOrCpf() {
      return emailOrCpf;
    }

    public void setEmailOrCpf(String emailOrCpf) {
      this.emailOrCpf = emailOrCpf;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    
}
