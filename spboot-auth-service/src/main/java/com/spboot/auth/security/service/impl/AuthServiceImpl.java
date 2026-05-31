package com.spboot.auth.security.service.impl;

import com.spboot.auth.dto.UserDto;
import com.spboot.auth.security.service.AuthService;
import com.spboot.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private  final PasswordEncoder passwordEncoder;


    @Override
    public UserDto registerUser(UserDto userDto) {
        //logic
        //verify email
        // Validate email
        //verify password
        //default roles
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        UserDto registeredUser =  userService.createUser(userDto);
        System.out.println("registeredUser :"+registeredUser);
        return  registeredUser;
    }
}