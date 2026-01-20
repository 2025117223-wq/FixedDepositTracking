package com.fd.controller;

import com.fd.dto.LoginRequest;
import org.springframework.web.bind.annotation.RequestParam;
import com.fd.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private RestTemplate restTemplate;  // Autowired RestTemplate for API calls to auth-service

    // Handle GET request to show the login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // Return login.jsp page
    }

    // Handle POST request when the login form is submitted
    @PostMapping("/login")
    public String handleLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        // Validate the email and password
        if (isBlank(email) || isBlank(password)) {
            model.addAttribute("error", "Please enter email and password.");
            return "login";  // Return to login page if validation fails
        }

        String emailClean = email.trim().toLowerCase();
        String passwordClean = password.trim();

        // Prepare login request payload
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(emailClean);
        loginRequest.setPassword(passwordClean);

        // Define the URL for the auth-service login API
        String authServiceUrl = "http://localhost:8081/auth/login"; // Change to your auth-service URL in production

        try {
            // Call auth-service to validate login credentials
            ResponseEntity<LoginResponse> response =
                    restTemplate.postForEntity(
                            authServiceUrl,
                            loginRequest,
                            LoginResponse.class
                    );

            LoginResponse loginResponse = response.getBody();

            // If login fails or token is not returned
            if (loginResponse == null || loginResponse.getToken() == null) {
                model.addAttribute("error", "Invalid email or password.");
                return "login";  // Return to login page if login fails
            }

            // Store JWT token and user info in session
            session.setAttribute("JWT_TOKEN", loginResponse.getToken());
            session.setAttribute("staffName", loginResponse.getName());
            session.setAttribute("staffRole", loginResponse.getRole());

            // Redirect to dashboard after successful login
            return "redirect:/dashboard";  // Redirect to the dashboard page

        } catch (Exception ex) {
            // In case of any error in contacting auth-service
            model.addAttribute("error", "Auth service is unavailable or an error occurred.");
            return "login";  // Return to login page in case of an exception
        }
    }

    // Helper method to check if the input is blank
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
