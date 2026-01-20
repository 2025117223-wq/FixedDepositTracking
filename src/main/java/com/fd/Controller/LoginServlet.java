package com.fd.controller;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private StaffDAO staffDAO;

    // Show login form (GET request)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // This returns login.jsp
    }

    // Handle login attempt (POST request)
    @PostMapping("/login")
    public String handleLogin(@RequestParam String email, @RequestParam String password, Model model, HttpSession session) {
        // Basic validation
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Please enter email and password.");
            return "login";  // Return to the login page if validation fails
        }

        String emailClean = email.trim().toLowerCase();
        String passwordClean = password.trim();

        try {
            // Attempt to login using the DAO
            Staff staff = staffDAO.login(emailClean, passwordClean);

            if (staff == null) {
                model.addAttribute("error", "Invalid email or password.");
                return "login";  // Return to the login page if login fails
            }

            // Check if account is active
            if (staff.getStatus() != null && !"Active".equalsIgnoreCase(staff.getStatus())) {
                model.addAttribute("error", "Your account is not active. Please contact admin.");
                return "login";  // Return to login page if account is not active
            }

            // Create session and store staff information
            session.setAttribute("loggedStaff", staff);
            session.setAttribute("staffId", staff.getStaffId());
            session.setAttribute("staffName", staff.getName());
            session.setAttribute("staffEmail", staff.getEmail());
            session.setAttribute("staffRole", staff.getRole());
            session.setAttribute("staffStatus", staff.getStatus());
            session.setAttribute("managerId", staff.getManagerId());

            // Redirect to the Dashboard after successful login
            return "redirect:/dashboard";  // Redirect to the dashboard page

        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "login";  // Return to login page if an exception occurs
        }
    }
}
