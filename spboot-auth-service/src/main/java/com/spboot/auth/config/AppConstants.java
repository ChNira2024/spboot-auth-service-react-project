package com.spboot.auth.config;

public class AppConstants {


    public static final String[] AUTH_PUBLIC_URLS = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-ui/index.html"
    };

    // USER endpoints (normal users)
    public static final String[] AUTH_USER_URLS = {
            "/api/v1/hello/**"
    };

    // ADMIN endpoints
    public static final String[] AUTH_ADMIN_URLS = {
            "/api/v1/users/**"
    };

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    public static final String GUEST_ROLE = "GUEST";


}