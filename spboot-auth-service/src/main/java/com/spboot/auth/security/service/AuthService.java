package com.spboot.auth.security.service;

import com.spboot.auth.dto.UserDto;

public interface AuthService {
    UserDto registerUser(UserDto userDto);

}