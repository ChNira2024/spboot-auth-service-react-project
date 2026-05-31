package com.spboot.auth.security.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.spboot.auth.entity.User;
import com.spboot.auth.repository.UserRepository;
import com.spboot.auth.security.service.JwtService2;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter2 extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter2.class);
    private final JwtService2 jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        log.info("HEADER: " + header);

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            Claims claims = jwtService.extractClaims(token);
            log.info("Token type: {}", claims.get("typ"));
            if (!jwtService.isAccessToken(claims)) {
                filterChain.doFilter(request, response);
                return;
            }

            //UUID userId = jwtService.getUserId(claims);
            UUID userId = UUID.fromString(claims.getSubject());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (!user.isEnable()) {
                throw new RuntimeException("User disabled");
            }
           /* List<GrantedAuthority> authorities =
                    Optional.ofNullable(user.getRoles())
                            .orElse(Set.of())
                            .stream()
                            .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.getName()))
                            .toList();
        */
            //List<GrantedAuthority> authorities = Collections.emptyList();
            List<GrantedAuthority> authorities =
                    user.getRoles()
                            .stream()
                            .map(role -> (GrantedAuthority) () -> role.getName())
                            .toList();
            System.out.println("Authorities: " + authorities);
       /*
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            Collections.emptyList()   // ✅ NO roles access
                    );
        */
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            authorities   // ✅ roles added
                    );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            String message = "Invalid token";

            if (e instanceof ExpiredJwtException) {
                message = "Session expired. Please login again";
            }

            String json = new ObjectMapper().writeValueAsString(
                    Map.of(
                            "status", 401,
                            "error", "Unauthorized",
                            "message", message,
                            "path", request.getRequestURI()
                    )
            );

            response.getWriter().write(json);
            return;
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/refresh");
    }
}