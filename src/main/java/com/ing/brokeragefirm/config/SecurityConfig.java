package com.ing.brokeragefirm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/orders/**").permitAll()
                )
                .httpBasic(withDefaults ->
                        withDefaults.authenticationEntryPoint(authenticationEntryPoint()));
        return http.build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomBasicAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}