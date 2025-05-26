package com.finals.cinema.security;

import com.finals.cinema.view.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/signin", "/register", "cinemas", "/main", "/images/**", "/img/**", "/styles/**", "/VAADIN/**").permitAll()
                        .anyRequest().authenticated()
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