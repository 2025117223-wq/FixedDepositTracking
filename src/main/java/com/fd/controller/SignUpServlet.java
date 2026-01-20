package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;

/**
 * SignUpServlet - FINAL VERSION
 * Matches YOUR Staff.java model exactly
 * For Tomcat 10+ (Jakarta EE)
 */
@WebServlet("/SignUpServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,  // 1MB
    maxFileSize = 5 * 1024 * 1024,    // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class SignUpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;

    @Override
    public void init() {
        staffDAO = new StaffDAO();
        System.out.println("========================================");
        System.out.println("✅ SignUpServlet INITIALIZED");
        System.out.println("========================================");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("========================================");
        System.out.println("📝 SignUpServlet - New User Registration");
        System.out.println("========================================");

        try {
            // Get form parameters
            String fullName = request.getParameter("name");
            String phoneNumber = request.getParameter("phone");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String homeAddress = request.getParameter("address");
            String staffRole = request.getParameter("role");
            String managerIdStr = request.getParameter("managerId");

            System.out.println("📋 Registration Details:");
            System.out.println("   Name: " + fullName);
            System.out.println("   Email: " + email);
            System.out.println("   Role: " + staffRole);
            System.out.println("   Manager: " + (managerIdStr != null && !managerIdStr.isEmpty() ? managerIdStr : "None"));

            // Get profile picture
            Part profilePart = request.getPart("profilePicture");

            // Basic validation
            if (isBlank(fullName) || isBlank(phoneNumber) || isBlank(email) ||
                isBlank(password) || isBlank(confirmPassword) ||
                isBlank(homeAddress) || isBlank(staffRole) ||
                profilePart == null || profilePart.getSize() == 0) {
                
                System.err.println("❌ Validation failed: Missing required fields");
                request.setAttribute("error", "Please fill in all required fields.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            // Password validation
            if (password.trim().length() < 6) {
                System.err.println("❌ Validation failed: Password too short");
                request.setAttribute("error", "Password must be at least 6 characters.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            if (!password.equals(confirmPassword)) {
                System.err.println("❌ Validation failed: Passwords don't match");
                request.setAttribute("error", "Passwords do not match.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            // Manager ID parse (optional)
            int managerId = 0; // 0 means no manager
            if (!isBlank(managerIdStr)) {
                try {
                    managerId = Integer.parseInt(managerIdStr);
                    if (managerId < 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    System.err.println("❌ Validation failed: Invalid manager ID");
                    request.setAttribute("error", "Invalid manager selected.");
                    request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                    return;
                }
            }

            // Profile picture validation
            String contentType = profilePart.getContentType();
            if (contentType == null ||
                !(contentType.equalsIgnoreCase("image/jpeg") ||
                  contentType.equalsIgnoreCase("image/jpg") ||
                  contentType.equalsIgnoreCase("image/png"))) {
                
                System.err.println("❌ Validation failed: Invalid file type");
                request.setAttribute("error", "Profile picture must be JPEG or PNG.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            // Check if email already exists
            if (staffDAO.emailExists(email.trim())) {
                System.err.println("❌ Registration failed: Email already exists");
                request.setAttribute("error", "Email already registered. Please use a different email.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            // Read profile picture as bytes
            byte[] pictureBytes = null;
            try (InputStream inputStream = profilePart.getInputStream()) {
                pictureBytes = inputStream.readAllBytes();
                System.out.println("📷 Profile picture size: " + pictureBytes.length + " bytes");
            }

            // Hash the password before saving
            String hashedPassword = BCrypt.hashpw(password.trim(), BCrypt.gensalt());

            // Create Staff object - FINAL VERSION using YOUR model
            Staff staff = new Staff();
            staff.setName(fullName.trim());              // ✅ YOUR MODEL
            staff.setPhone(phoneNumber.trim());          // ✅ YOUR MODEL
            staff.setAddress(homeAddress.trim());        // ✅ YOUR MODEL
            staff.setEmail(email.trim().toLowerCase());  // ✅ YOUR MODEL
            staff.setRole(staffRole.trim());             // ✅ YOUR MODEL
            staff.setPassword(hashedPassword);           // ✅ Hash the password
            staff.setStatus("Active");                   // ✅ YOUR MODEL
            staff.setManagerId(managerId);               // ✅ YOUR MODEL
            staff.setProfilePicture(pictureBytes);       // ✅ YOUR MODEL

            // Insert into database
            System.out.println("💾 Inserting staff into database...");
            boolean success = staffDAO.registerStaff(staff);

            if (success) {
                System.out.println("✅ REGISTRATION SUCCESSFUL");
                System.out.println("   Email: " + email);
                System.out.println("   Role: " + staffRole);
                System.out.println("========================================");
                
                // Redirect to login with success message
                response.sendRedirect("Login.jsp?signup=success");
            } else {
                System.err.println("❌ REGISTRATION FAILED - Database insert failed");
                request.setAttribute("error", "Sign up failed. Please try again.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("❌ Exception during registration: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
        }
    }

    /**
     * Check if string is blank (null or empty)
     */
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
