package com.finals.cinema.security;

import com.finals.cinema.view.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/signin", "/register", "/cinemas/**", "/main", "/images/**", "/img/**", "/styles/**", "/VAADIN/**", "/access_denied", "/frontend/**", "/movie-details/**", "/x123abc/**", "/icons/**", "/h2-console/**", "/test/**").permitAll()
                        .requestMatchers("/admin_panel").access(new WebExpressionAuthorizationManager("hasRole('ADMIN')"))
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Handles authenticated but unauthorized users
                            response.sendRedirect("/access_denied");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Handles unauthenticated users
                            response.sendRedirect("/access_denied");
                        })
                )
                .csrf(csrf -> csrf.disable()
                )
                .securityContext(context -> context
                        .requireExplicitSave(false)
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}