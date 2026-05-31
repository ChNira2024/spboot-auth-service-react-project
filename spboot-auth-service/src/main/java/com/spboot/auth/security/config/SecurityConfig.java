//package com.spboot.auth.security.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.spboot.auth.config.AppConstants;
//import com.spboot.auth.dto.ApiError;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.util.Map;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//    private AuthenticationSuccessHandler successHandler;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationSuccessHandler successHandler) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.successHandler = successHandler;
//    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults()).sessionManagement(sm -> sm.
//                sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//               // .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
//        .authorizeHttpRequests(authorizeHttpRequests ->
//                authorizeHttpRequests.requestMatchers(AppConstants.AUTH_PUBLIC_URLS).permitAll()
//                        .anyRequest().authenticated())
//                .oauth2Login(oauth2 -> oauth2.successHandler(successHandler).failureHandler(null))
//                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, e) -> {
//                    //error message
//                    e.printStackTrace();
//                    response.setStatus(401);
//                    response.setContentType("application/json");
//                    String message = e.getMessage();
//                    String error = (String) request.getAttribute("error");
//                    if (error != null) {
//                        message = error;
//                    }
//                    //Map<String, Object> errorMap = Map.of("message", message, "statusCode",404);
//                    var apiError = ApiError.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized Access", message, request.getRequestURI(), true);
//                    var objectMapper = new ObjectMapper();
//                    response.getWriter().write(objectMapper.writeValueAsString(apiError));
//                })).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//}