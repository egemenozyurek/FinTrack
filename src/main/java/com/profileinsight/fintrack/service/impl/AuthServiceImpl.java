package com.profileinsight.fintrack.service.impl;

import com.profileinsight.fintrack.dto.request.LoginRequest;
import com.profileinsight.fintrack.dto.request.RegisterRequest;
import com.profileinsight.fintrack.dto.response.AuthResponse;
import com.profileinsight.fintrack.entity.User;
import com.profileinsight.fintrack.exception.BusinessException;
import com.profileinsight.fintrack.repository.UserRepository;
import com.profileinsight.fintrack.security.JwtService;
import com.profileinsight.fintrack.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("Bu e-posta adresi zaten kayıtlı.");
            }

            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    // Şifreyi ASLA düz metin olarak kaydetme — her zaman hash'le.
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .build();

            User saved = userRepository.save(user);

            String token = jwtService.generateToken(saved.getId(), saved.getEmail());

            return AuthResponse.builder()
                    .accessToken(token)
                    .userId(saved.getId())
                    .name(saved.getName())
                    .email(saved.getEmail())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager şifreyi otomatik kontrol eder.
        // Yanlışsa BadCredentialsException fırlatır — bunu da
        // GlobalExceptionHandler'a eklememiz gerekecek (aşağıda not var).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("E-posta veya şifre hatalı."));

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}