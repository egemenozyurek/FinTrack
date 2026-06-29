package com.profileinsight.fintrack.config;

import com.profileinsight.fintrack.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Tüm güvenlik kurallarının tanımlandığı yer.
 *
 * Kritik kararlar:
 * - SessionCreationPolicy.STATELESS: Spring hiçbir session oluşturmasın.
 *   JWT zaten her isteğin kendi kimliğini taşıyor, session'a gerek yok.
 * - CSRF kapalı: CSRF koruması, tarayıcı + cookie tabanlı session'lar için var.
 *   Biz JWT + mobil istemci kullandığımız için CSRF riski yok, kapatıyoruz.
 * - JwtAuthenticationFilter, Spring'in kendi login filtresinden ÖNCE çalışmalı.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Herkese açık: login, register, Swagger
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/webjars/**"
                        ).permitAll()
                        // Geri kalan HER ŞEY token gerektirir
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt: şifreyi tek yönlü hash'ler, geri çözülemez.
        // Her hash'leme farklı "salt" kullanır — aynı şifre, farklı hash üretir.
        // Bu, rainbow table saldırılarına karşı korur.
        return new BCryptPasswordEncoder();
    }
}