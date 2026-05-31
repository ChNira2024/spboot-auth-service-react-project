package com.spboot.auth.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spboot.auth.config.AppConstants;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig2 {

    private final JwtAuthenticationFilter2 jwtAuthenticationFilter;
    private final DaoAuthenticationProvider authenticationProvider;
    private final AuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(AppConstants.AUTH_PUBLIC_URLS).permitAll()
                        .requestMatchers(AppConstants.AUTH_ADMIN_URLS).hasRole(AppConstants.ADMIN_ROLE)
                        .requestMatchers(AppConstants.AUTH_USER_URLS).hasAnyRole(AppConstants.USER_ROLE, AppConstants.ADMIN_ROLE)
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.successHandler(successHandler)
                        .failureHandler((req, res, ex) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");

                            new ObjectMapper().writeValue(res.getWriter(), Map.of(
                                    "status", 401,
                                    "error", "Unauthorized",
                                    "message", "OAuth login failed",
                                    "path", req.getRequestURI()
                            ));
                        }))
/*
                        .failureHandler((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().write("OAuth login failed");
                }))
                */

                // VERY IMPORTANT
                .authenticationProvider(authenticationProvider)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");

                            String json = new ObjectMapper().writeValueAsString(
                                    Map.of(
                                            "status", 401,
                                            "error", "Unauthorized",
                                            "message", "Authentication required",
                                            "path", req.getRequestURI()
                                    )
                            );

                            res.getWriter().write(json);
                        })

                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");

                            String json = new ObjectMapper().writeValueAsString(
                                    Map.of(
                                            "status", 403,
                                            "error", "Forbidden",
                                            "message", "You don't have permission to access this resource",
                                            "path", req.getRequestURI()
                                    )
                            );

                            res.getWriter().write(json);
                        })
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}