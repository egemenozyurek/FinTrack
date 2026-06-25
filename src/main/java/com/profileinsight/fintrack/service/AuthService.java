package com.profileinsight.fintrack.service;

import com.profileinsight.fintrack.dto.request.LoginRequest;
import com.profileinsight.fintrack.dto.request.RegisterRequest;
import com.profileinsight.fintrack.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
