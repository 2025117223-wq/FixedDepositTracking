package com.fixeddeposit.authservice.controller;

import com.fixeddeposit.authservice.dto.LoginRequest;
import com.fixeddeposit.authservice.dto.RegisterRequest;
import com.fixeddeposit.authservice.model.Staff;
import com.fixeddeposit.authservice.repository.StaffRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final StaffRepository staffRepository;

    public AuthController(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String email = req.email == null ? "" : req.email.trim().toLowerCase();
        String password = req.password == null ? "" : req.password.trim();

        Optional<Staff> staffOpt = staffRepository.findByStaffemail(email);
        if (staffOpt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");

        Staff staff = staffOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(staff.getStaffstatus())) {
            return ResponseEntity.status(403).body("Staff inactive");
        }

        // PLAIN compare (ikut sistem kau sekarang)
        if (!password.equals(staff.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        return ResponseEntity.ok("OK");
    }


}
