package com.challenge.paymengateway.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.challenge.paymengateway.application.dto.BillingResponseDTO;
import com.challenge.paymengateway.application.dto.CancelResponseDTO;
import com.challenge.paymengateway.application.dto.PaymentRequestDTO;
import com.challenge.paymengateway.application.dto.PaymentResponseDTO;
import com.challenge.paymengateway.application.model.Account;
import com.challenge.paymengateway.application.model.Billing;
import com.challenge.paymengateway.application.model.User;
import com.challenge.paymengateway.application.repository.AccountRepository;
import com.challenge.paymengateway.application.repository.BillingRepository;
import com.challenge.paymengateway.application.repository.PaymentRepository;
import com.challenge.paymengateway.application.repository.UserRepository;
import com.challenge.paymengateway.common.components.PaymentValidator;
import com.challenge.paymengateway.common.enums.PaymentMethods;
import com.challenge.paymengateway.common.enums.StatusCobranca;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentValidator paymentValidator;

    @InjectMocks
    private BillingService billingService;

    private User sender;
    private User receiver;
    private Account receiverAccount;
    private Billing billing;

    @BeforeEach
    void setup() {
        sender = new User();
        sender.setId(1);
        sender.setName("João");
        sender.setEmail("joao@email.com");
        sender.setCpf("12345678909");

        receiver = new User();
        receiver.setId(2);
        receiver.setName("Maria");
        receiver.setEmail("maria@email.com");
        receiver.setCpf("98765432100");

         
        receiverAccount = new Account();
        receiverAccount.setId(2);
        receiverAccount.setUser(receiver);
        receiverAccount.setBalance(BigDecimal.valueOf(200).setScale(4));

        billing = new Billing();
        billing.setId(1);
        billing.setSender(sender);
        billing.setReceiver(receiver);
        billing.setValue(BigDecimal.valueOf(100).setScale(4));
        billing.setDescription("Teste");
        billing.setStatus(StatusCobranca.PENDENTE);
    }

    @Test
    void shouldGetBillingByUserId() {
        when(billingRepository.findBySenderIdAndStatusOptional(1, StatusCobranca.PENDENTE))
                .thenReturn(Optional.of(List.of(billing)));

        List<BillingResponseDTO> list = billingService.getBillingByUserId(1, StatusCobranca.PENDENTE, true);

        assertEquals(1, list.size());
        assertEquals(billing.getDescription(), list.get(0).description());
    }

    @Test
    void shouldProcessPaymentWithSaldo() {
      User sender = new User();
      sender.setId(1);
      sender.setName("Umberto");

      User receiver = new User();
      receiver.setId(2);
      receiver.setName("Doisberto");

      Billing billing = new Billing();
      billing.setId(100);
      billing.setSender(sender);
      billing.setReceiver(receiver);
      billing.setValue(BigDecimal.valueOf(100).setScale(4));
      billing.setStatus(StatusCobranca.PENDENTE);

      Account payerAccount = new Account();
      payerAccount.setId(10);
      payerAccount.setUser(receiver); // quem paga
      payerAccount.setBalance(BigDecimal.valueOf(500).setScale(4));

      Account receiverAccount = new Account();
      receiverAccount.setId(11);
      receiverAccount.setUser(sender); // quem recebe
      receiverAccount.setBalance(BigDecimal.valueOf(200).setScale(4));

      PaymentRequestDTO dto = new PaymentRequestDTO(100, PaymentMethods.SALDO, null, null, null);

      when(billingRepository.findById(100)).thenReturn(Optional.of(billing));
      when(accountRepository.findByUserId(receiver.getId())).thenReturn(Optional.of(payerAccount));
      when(accountRepository.findByUserId(sender.getId())).thenReturn(Optional.of(receiverAccount));
      when(paymentRepository.findByBillingId(100)).thenReturn(Optional.empty());
      when(billingRepository.save(any())).thenAnswer(i -> i.getArgument(0));
      when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));
      when(paymentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

      PaymentResponseDTO response = billingService.processPayment(receiver.getId(), dto);

      assertNotNull(response);
      assertEquals("SUCCESS", response.status());
      assertEquals(BigDecimal.valueOf(400).setScale(4), payerAccount.getBalance());      // 500 - 100
      assertEquals(BigDecimal.valueOf(300).setScale(4), receiverAccount.getBalance());   // 200 + 100
    }

    @Test
    void shouldCancelPendingBilling() {
      when(billingRepository.findById(1)).thenReturn(Optional.of(billing));
      when(billingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

      CancelResponseDTO response = billingService.cancelBilling(1, 1);

      assertEquals(StatusCobranca.CANCELADA, response.status());
    }
}
