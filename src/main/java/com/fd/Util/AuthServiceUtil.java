package com.fd.util;

import com.fd.dto.LoginRequest;
import com.fd.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthServiceUtil {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public AuthServiceUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            // Send the login request to the auth-service using POST
            return restTemplate.postForObject(authServiceUrl, loginRequest, LoginResponse.class);
        } catch (Exception e) {
            // Handle any exceptions here and throw an appropriate runtime exception
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }
}
