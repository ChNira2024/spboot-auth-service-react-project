package com.spboot.hello.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String hello(Authentication authentication) {

        return "Hello " + authentication.getName() +
                " | Welcome to Hello Service";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminHello() {
        return "Hello Admin";
    }
}
