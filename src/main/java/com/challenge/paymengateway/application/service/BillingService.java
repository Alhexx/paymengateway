package com.challenge.paymengateway.application.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.challenge.paymengateway.application.dto.BillingRequestDTO;
import com.challenge.paymengateway.application.dto.BillingResponseDTO;
import com.challenge.paymengateway.application.model.Billing;
import com.challenge.paymengateway.application.model.User;
import com.challenge.paymengateway.application.repository.BillingRepository;
import com.challenge.paymengateway.application.repository.UserRepository;
import com.challenge.paymengateway.common.enums.StatusCobranca;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class BillingService {
  private final BillingRepository billingRepository;
  private final UserRepository userRepository;

  public BillingService(BillingRepository billingRepository, UserRepository userRepository) {
    this.billingRepository = billingRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public BillingResponseDTO createBilling(Integer userId, BillingRequestDTO billingRequestDTO) {
    User sender = userRepository.findById(userId).orElseThrow(() -> 
                                              new EntityNotFoundException("Usuário não encontrado com ID: " + userId
                                            ));
    User reciever = userRepository.findByCpf(billingRequestDTO.recieverCPF()).orElseThrow(() -> 
                                              new EntityNotFoundException("Conta não encontrada para o CPF: " + billingRequestDTO.recieverCPF()
                                            ));

    if (sender.getId().equals(reciever.getId())) {
      throw new IllegalArgumentException("O remetente e o destinatário não podem ser a mesma pessoa.");
    }                                        

    Billing billing = new Billing();
    billing.setDescription(billingRequestDTO.description());
    billing.setValue(billingRequestDTO.value());
    billing.setReciever(reciever);
    billing.setSender(sender);

    billingRepository.save(billing);
    return BillingResponseDTO.from(billing);
  }

  public List<BillingResponseDTO> getBillingByUserId(Integer userId, StatusCobranca status, boolean isSender) {
    Supplier<Optional<List<Billing>>> query = isSender
        ? () -> billingRepository.findBySenderIdAndStatusOptional(userId, status)
        : () -> billingRepository.findByRecieverIdAndStatusOptional(userId, status);

    List<Billing> billings = query.get().orElseThrow(() -> new EntityNotFoundException("Nenhuma cobrança encontrada para o usuário com ID: " + userId));
    
    return billings.stream().map(BillingResponseDTO::from).toList();
  }
}
