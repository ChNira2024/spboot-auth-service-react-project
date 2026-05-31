package com.spboot.auth.security.service;

import com.spboot.auth.dto.UserDto;
import com.spboot.auth.dto.UserRequestDto;
import com.spboot.auth.dto.UserResponseDto;

public interface AuthService2 {
    UserResponseDto registerUser(UserRequestDto
                                 userDto);

}