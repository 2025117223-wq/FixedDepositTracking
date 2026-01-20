package com.fixeddeposit.service;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    public String login(LoginRequest loginRequest) {
        // Hardcoded authentication for now. Replace with database validation logic
        if ("admin".equals(loginRequest.getUsername()) && "password".equals(loginRequest.getPassword())) {
            return "JWT_Token";  // Replace with actual JWT generation logic
        }
        return "Invalid credentials";
    }
}
