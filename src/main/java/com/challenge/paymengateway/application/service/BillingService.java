package com.challenge.paymengateway.application.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.challenge.paymengateway.application.dto.BillingRequestDTO;
import com.challenge.paymengateway.application.dto.BillingResponseDTO;
import com.challenge.paymengateway.application.dto.CancelResponseDTO;
import com.challenge.paymengateway.application.dto.PaymentRequestDTO;
import com.challenge.paymengateway.application.dto.PaymentResponseDTO;
import com.challenge.paymengateway.application.model.Account;
import com.challenge.paymengateway.application.model.Billing;
import com.challenge.paymengateway.application.model.Payment;
import com.challenge.paymengateway.application.model.User;
import com.challenge.paymengateway.application.repository.AccountRepository;
import com.challenge.paymengateway.application.repository.BillingRepository;
import com.challenge.paymengateway.application.repository.PaymentRepository;
import com.challenge.paymengateway.application.repository.UserRepository;
import com.challenge.paymengateway.common.components.PaymentValidator;
import com.challenge.paymengateway.common.enums.PaymentMethods;
import com.challenge.paymengateway.common.enums.StatusCobranca;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class BillingService {
  private final BillingRepository billingRepository;
  private final UserRepository userRepository;
  private final AccountRepository accountRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentValidator paymentValidator;

  public BillingService(BillingRepository billingRepository, UserRepository userRepository, AccountRepository accountRepository, PaymentValidator paymentValidator, PaymentRepository paymentRepository) {
    this.billingRepository = billingRepository;
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
    this.paymentRepository = paymentRepository;
    this.paymentValidator = paymentValidator;
  }

  @Transactional
  public BillingResponseDTO createBilling(Integer userId, BillingRequestDTO billingRequestDTO) {
    User sender = userRepository.findById(userId).orElseThrow(() -> 
                                              new EntityNotFoundException("Usuário não encontrado com ID: " + userId
                                            ));
    User receiver = userRepository.findByCpf(billingRequestDTO.receiverCPF()).orElseThrow(() -> 
                                              new EntityNotFoundException("Conta não encontrada para o CPF: " + billingRequestDTO.receiverCPF()
                                            ));

    if (sender.getId().equals(receiver.getId())) {
      throw new IllegalArgumentException("O remetente e o destinatário não podem ser a mesma pessoa.");
    }                                        

    Billing billing = new Billing();
    billing.setDescription(billingRequestDTO.description());
    billing.setValue(billingRequestDTO.value());
    billing.setReceiver(receiver);
    billing.setSender(sender);

    billingRepository.save(billing);
    return BillingResponseDTO.from(billing);
  }

  public List<BillingResponseDTO> getBillingByUserId(Integer userId, StatusCobranca status, boolean isSender) {
    Supplier<Optional<List<Billing>>> query = isSender
        ? () -> billingRepository.findBySenderIdAndStatusOptional(userId, status)
        : () -> billingRepository.findByReceiverIdAndStatusOptional(userId, status);

    List<Billing> billings = query.get().orElseThrow(() -> new EntityNotFoundException("Nenhuma cobrança encontrada para o usuário com ID: " + userId));
    
    return billings.stream().map(BillingResponseDTO::from).toList();
  }


  @Transactional
  public PaymentResponseDTO processPayment(Integer userId, PaymentRequestDTO paymentRequestDTO) {
      Billing billing = billingRepository.findById(paymentRequestDTO.billingId())
          .orElseThrow(() -> new EntityNotFoundException(
              "Cobrança não encontrada com ID: " + paymentRequestDTO.billingId()
          ));

      // O usuário logado é o criador da cobrança (quem vai receber)
      if (billing.getSender().getId().equals(userId)) {
          throw new IllegalArgumentException("Usuário não autorizado a fazer este pagamento.");
      }

      if (billing.getStatus() == StatusCobranca.PAGA) {
          throw new IllegalArgumentException("Cobrança já foi paga.");
      }

      // Conta de quem vai pagar
      Account payerAccount = accountRepository.findByUserId(userId)
          .orElseThrow(() -> new EntityNotFoundException(
              "Conta não encontrada para o usuário (pagador) com ID: " + userId
          ));

      // Conta de quem vai receber
      Account receiverAccount = accountRepository.findByUserId(billing.getSender().getId())
          .orElseThrow(() -> new EntityNotFoundException(
              "Conta do remetente (recebedor do valor) não encontrada com ID: " + billing.getSender().getId()
          ));

      Payment payment = paymentRepository.findByBillingId(billing.getId()).orElse(new Payment());
      payment.setBilling(billing);
      payment.setPaymentMethod(paymentRequestDTO.paymentMethod());

      String statusMessage = "Pagamento processado com sucesso";
      String statusResponse = "SUCCESS";
      boolean success = false;

      if (paymentRequestDTO.paymentMethod() == PaymentMethods.SALDO) {
          if (payerAccount.getBalance().compareTo(billing.getValue()) < 0) {
              statusMessage = "Saldo insuficiente para processar o pagamento.";
              statusResponse = "FAILED";
          } else {
              payerAccount.setBalance(payerAccount.getBalance().subtract(billing.getValue()));
              receiverAccount.setBalance(receiverAccount.getBalance().add(billing.getValue()));
              success = true;
          }
      } else if (paymentRequestDTO.paymentMethod() == PaymentMethods.CARTAO) {
          try {
              boolean approved = paymentValidator.validateOperation();
              if (approved) {
                  receiverAccount.setBalance(receiverAccount.getBalance().add(billing.getValue()));
                  success = true;
              } else {
                  statusMessage = "Pagamento não aceito pelo validador.";
                  statusResponse = "FAILED";
              }
          } catch (Exception e) {
              statusMessage = "Erro ao validar pagamento: " + e.getMessage();
              statusResponse = "FAILED";
          }
      } else {
          throw new IllegalArgumentException("Método de pagamento não suportado.");
      }

      payment.setSuccess(success);
      billing.setStatus(success ? StatusCobranca.PAGA : StatusCobranca.PENDENTE);

      billingRepository.save(billing);
      paymentRepository.save(payment);
      accountRepository.save(payerAccount);
      accountRepository.save(receiverAccount);

      return PaymentResponseDTO.from(statusResponse, statusMessage, paymentRequestDTO.paymentMethod());
  }

  @Transactional
  public CancelResponseDTO cancelBilling(Integer userId, Integer billingId) {
    Billing billing = billingRepository.findById(billingId)
          .orElseThrow(() -> new EntityNotFoundException(
              "Cobrança não encontrada com ID: " + billingId
          ));

		Payment payment = paymentRepository.findByBillingId(billingId).orElse(null);

		if (!(billing.getSender().getId().equals(userId) || billing.getReceiver().getId().equals(userId))) {
			throw new IllegalArgumentException("Usuário não autorizado a cancelar esta cobrança.");
		}

		if (billing.getStatus() == StatusCobranca.CANCELADA) {
			throw new IllegalArgumentException("Cobrança já foi cancelada.");
		} 

		if (billing.getStatus() == StatusCobranca.PENDENTE) {
			billing.setStatus(StatusCobranca.CANCELADA);
			billingRepository.save(billing);
			return CancelResponseDTO.from(billing.getStatus(), "Cobrança pendente cancelada com sucesso.", billingId);
    }

		if (billing.getStatus() == StatusCobranca.PAGA && payment != null) {
			Account senderAccount = accountRepository.findByUserId(billing.getSender().getId())
							.orElseThrow(() -> new EntityNotFoundException(
									"Conta do remetente não encontrada"
							));
							
			if (payment.getPaymentMethod() == PaymentMethods.SALDO) {
					Account payerAccount = accountRepository.findByUserId(billing.getReceiver().getId())
							.orElseThrow(() -> new EntityNotFoundException(
									"Conta do pagador não encontrada para estorno."
							));

					payerAccount.setBalance(payerAccount.getBalance().add(billing.getValue()));
					senderAccount.setBalance(senderAccount.getBalance().subtract(billing.getValue()));
					accountRepository.save(payerAccount);
					accountRepository.save(senderAccount);

					payment.setCancelled(true);
			} else if (payment.getPaymentMethod() == PaymentMethods.CARTAO) {
				boolean approved = paymentValidator.validateOperation();
				if (!approved) {
					throw new IllegalArgumentException("Falha ao validar estorno do pagamento via cartão.");
				}

				senderAccount.setBalance(senderAccount.getBalance().subtract(billing.getValue()));
				accountRepository.save(senderAccount);

				payment.setCancelled(true);
			}
			
			billing.setStatus(StatusCobranca.CANCELADA);
			billingRepository.save(billing);
			paymentRepository.save(payment);
			return CancelResponseDTO.from(billing.getStatus(), "Cobrança paga cancelada e estorno realizado com sucesso.", billingId);
		}
		throw new IllegalArgumentException("Estado da cobrança não permite cancelamento.");
  }
}
