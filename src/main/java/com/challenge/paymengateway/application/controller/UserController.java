package com.challenge.paymengateway.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.paymengateway.application.dto.UserCreateDTO;
import com.challenge.paymengateway.application.dto.UserResponseDTO;
import com.challenge.paymengateway.application.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO dto) {
      UserResponseDTO createdUser = userService.createUser(dto);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }  
}
