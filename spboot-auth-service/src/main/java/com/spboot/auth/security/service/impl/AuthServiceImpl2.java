package com.spboot.auth.security.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spboot.auth.dto.UserRequestDto;
import com.spboot.auth.dto.UserResponseDto;
import com.spboot.auth.security.repository.RoleRepository;
import com.spboot.auth.security.service.AuthService2;
import com.spboot.auth.service.UserService2;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl2 implements AuthService2 {

    private final UserService2 userService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final RoleRepository roleRepository;


    @Override
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        //logic
        //verify email
        // Validate email
        //verify password
        //default roles
        userRequestDto.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        UserResponseDto registeredUser = userService.createUser(userRequestDto);
        try {
            System.out.println("registeredUser :" + objectMapper.writeValueAsString(registeredUser));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return registeredUser;
    }
}