package com.spboot.auth.security.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spboot.auth.dto.UserRequestDto;
import com.spboot.auth.dto.UserResponseDto;
import com.spboot.auth.entity.RefreshToken;
import com.spboot.auth.entity.User;
import com.spboot.auth.exception.InvalidCredentialsException;
import com.spboot.auth.repository.UserRepository;
import com.spboot.auth.security.dto.LoginRequest;
import com.spboot.auth.security.dto.RefreshTokenRequest;
import com.spboot.auth.security.dto.TokenResponse2;
import com.spboot.auth.security.repository.RefreshTokenRepository;
import com.spboot.auth.security.service.AuthService2;
import com.spboot.auth.security.service.CookieService;
import com.spboot.auth.security.service.JwtService2;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController2 {

    private final AuthService2 authService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService2 jwtService;
    private final ModelMapper mapper;
    private final CookieService cookieService;
    private ObjectMapper objectMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<TokenResponse2> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        try {
            // Let Spring handle authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Fetch user ONLY ONCE
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.isEnable()) {
            throw new RuntimeException("User is disabled");
        }

        // Generate tokens (same as your code)
        String jti = UUID.randomUUID().toString();

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, jti);

        cookieService.attachRefreshCookie(response, refreshToken,
                (int) jwtService.getRefreshTtlSeconds());

        cookieService.addNoStoreHeaders(response);
        UserResponseDto userResponse = mapper.map(user, UserResponseDto.class);
        TokenResponse2 tokenResponse = new TokenResponse2(
                accessToken,
                null,
                jwtService.getAccessTtlSeconds(),
                "Bearer",
                userResponse
        );
        return ResponseEntity.ok(tokenResponse);
    }

    // ================= REFRESH =================
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse2> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest body,
            HttpServletResponse response,
            HttpServletRequest request) throws JsonProcessingException {

        String refreshToken = readRefreshTokenFromRequest(body, request)
                .orElseThrow(() -> new BadCredentialsException("Refresh token is missing"));

        // ✅ parse once
        Claims claims;
        try {
            claims = jwtService.extractClaims(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Refresh Token");
        }

        if (!jwtService.isRefreshToken(claims)) {
            throw new BadCredentialsException("Invalid Refresh Token Type");
        }

        String jti = jwtService.getJti(claims);
        UUID userId = jwtService.getUserId(claims);

        RefreshToken storedToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));

        if (storedToken.isRevoked()) {
            throw new BadCredentialsException("Refresh token revoked");
        }

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        if (!storedToken.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Token user mismatch");
        }

        // 🔁 ROTATE TOKEN
        storedToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();

        RefreshToken newRefreshEntity = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshEntity);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user, newJti);

        cookieService.attachRefreshCookie(response, newRefreshToken,
                (int) jwtService.getRefreshTtlSeconds());

        cookieService.addNoStoreHeaders(response);
        UserResponseDto userResponse = mapper.map(user, UserResponseDto.class);

        TokenResponse2 tokenResponse = new TokenResponse2(
                newAccessToken,
                null,
                jwtService.getAccessTtlSeconds(),
                "Bearer",
                userResponse
        );

        System.out.println("TokenResponse2: " + tokenResponse.getClass());
        System.out.println(
                "TokenResponse2 JSON: " + objectMapper.writeValueAsString(tokenResponse)
        );

        return ResponseEntity.ok(tokenResponse);
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(userDto));
    }

    // ================= LOGOUT =================
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {

        readRefreshTokenFromRequest(null, request).ifPresent(token -> {
            try {
                Claims claims = jwtService.extractClaims(token);

                if (jwtService.isRefreshToken(claims)) {
                    String jti = jwtService.getJti(claims);

                    refreshTokenRepository.findByJti(jti).ifPresent(rt -> {
                        rt.setRevoked(true);
                        refreshTokenRepository.save(rt);
                    });
                }
            } catch (Exception ignored) {
            }
        });

        cookieService.clearRefreshCookie(response);
        cookieService.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ================= AUTHENTICATE =================
    private void authenticate(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid Username or Password");
        }
    }

    // ================= READ REFRESH TOKEN =================
    private Optional<String> readRefreshTokenFromRequest(
            RefreshTokenRequest body,
            HttpServletRequest request) {

        // 1. COOKIE
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> !v.isBlank())
                    .findFirst();
        }

        // 2. BODY
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return Optional.of(body.refreshToken());
        }

        // 3. HEADER
        String header = request.getHeader("X-Refresh-Token");
        if (header != null && !header.isBlank()) {
            return Optional.of(header.trim());
        }

        return Optional.empty();
    }
}