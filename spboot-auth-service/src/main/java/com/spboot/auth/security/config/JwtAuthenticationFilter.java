package com.spboot.auth.security.config;

import com.spboot.auth.helper.UserHelper;
import com.spboot.auth.repository.UserRepository;
import com.spboot.auth.security.service.JwtService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        logger.info("Authorization header : {}", header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (!jwtService.isAccessToken(token)) {
                    //message pass kar hai---
                    filterChain.doFilter(request, response);
                    return;
                }
                Jws<Claims> parse = jwtService.parse(token);
                Claims payload = parse.getPayload();
                String userId = payload.getSubject();
                UUID userUuid = UserHelper.parseUUID(userId);
                userRepository.findById(userUuid).ifPresent(user -> {
                    if (user.isEnable()) {
                        List<GrantedAuthority> authorities = user.getRoles() == null ? List.of() : user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        //final line : to set the authentication to security context
                        if (SecurityContextHolder.getContext().getAuthentication() == null) {
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                        else {
                            logger.warn("User with UUID {} is disabled", userUuid);
                            request.setAttribute("error", "User account is disabled");
                        }
                }else {
                    logger.warn("User with UUID {} not found in database", userUuid);
                    request.setAttribute("error", "User not found");
                }
            });
            }catch (ExpiredJwtException e) {
                logger.error("JWT Expired: {}", e.getMessage());
                request.setAttribute("error", "Token Expired");
            } catch (JwtException e) {
                // Catching JWT specific exceptions first
                logger.error("JWT Parsing failed: {}", e.getMessage());
                request.setAttribute("error", "Invalid Token Structure");
            } catch (Exception e) {
                // This captures database errors or code bugs, NOT just bad tokens
                logger.error("Unexpected filter error", e);
                request.setAttribute("error", "Internal Server Error");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/api/v1/auth");
    }
}