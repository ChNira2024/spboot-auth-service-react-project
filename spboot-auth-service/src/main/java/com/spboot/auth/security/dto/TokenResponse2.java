package com.spboot.auth.security.dto;

import com.spboot.auth.dto.UserResponseDto;

public record TokenResponse2(String accessToken, String refreshToken, long expiresIn, String tokenType, UserResponseDto user)
{

    public static TokenResponse2 of(String accessToken, String refreshToken, long expiresIn, UserResponseDto user) {
        return new TokenResponse2(accessToken, refreshToken, expiresIn, "Bearer", user);
    }

}