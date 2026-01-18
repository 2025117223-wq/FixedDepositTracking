package com.fixeddeposit.authservices.controller;

import com.fixeddeposit.authservices.dto.LoginRequest;
import com.fixeddeposit.authservices.dto.LoginResponse;
import com.fixeddeposit.authservices.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "Auth Service is running";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse res = authService.login(req.email(), req.password());
        return ResponseEntity.ok(res);
    }
}
