package com.challenge.paymengateway.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.challenge.paymengateway.application.dto.UserCreateDTO;
import com.challenge.paymengateway.application.dto.UserResponseDTO;
import com.challenge.paymengateway.application.model.Account;
import com.challenge.paymengateway.application.model.User;
import com.challenge.paymengateway.application.repository.AccountRepository;
import com.challenge.paymengateway.application.repository.UserRepository;


class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private UserService userService;

    private UserCreateDTO validDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder, accountRepository);

        validDto = new UserCreateDTO("João Silva", "joao@email.com", "123.456.789-09", "senha123");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setName(validDto.getName());
        savedUser.setEmail(validDto.getEmail());
        savedUser.setCpf(validDto.getCpf());
        savedUser.setPassword("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO result = userService.createUser(validDto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("João Silva", result.getName());
        assertEquals("joao@email.com", result.getEmail());

        verify(userRepository).save(any(User.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(validDto));
        assertEquals("E-mail já cadastrado", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(validDto));
        assertEquals("CPF já cadastrado", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCpfIsInvalid() {
        UserCreateDTO invalidCpfDto = new UserCreateDTO("Maria", "maria@email.com", "111.111.111-11", "1234");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.createUser(invalidCpfDto));
        assertEquals("CPF inválido", ex.getMessage());
    }
}
