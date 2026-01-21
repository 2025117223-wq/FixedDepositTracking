package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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

            // Get profile picture
            Part profilePart = request.getPart("profilePicture");

            // Basic validation
            if (isBlank(fullName) || isBlank(phoneNumber) || isBlank(email) ||
                isBlank(password) || isBlank(confirmPassword) ||
                isBlank(homeAddress) || isBlank(staffRole) ||
                profilePart == null || profilePart.getSize() == 0) {
                
                request.setAttribute("error", "Please fill in all required fields.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            // Password validation
            if (password.trim().length() < 6) {
                request.setAttribute("error", "Password must be at least 6 characters.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Passwords do not match.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            // Manager ID parse (optional)
            int managerId = 0;
            if (!isBlank(managerIdStr)) {
                try {
                    managerId = Integer.parseInt(managerIdStr);
                } catch (NumberFormatException ex) {
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
                
                request.setAttribute("error", "Profile picture must be JPEG or PNG.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            // Check if email already exists
            if (staffDAO.emailExists(email.trim())) {
                request.setAttribute("error", "Email already registered. Please use a different email.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            // Read profile picture as bytes
            byte[] pictureBytes = null;
            try (InputStream inputStream = profilePart.getInputStream()) {
                pictureBytes = inputStream.readAllBytes();
            }

            // Determine staff ID prefix based on role
            String staffIdPrefix = determineStaffPrefix(staffRole);

            // Create Staff object
            Staff staff = new Staff();
            staff.setName(fullName.trim());
            staff.setPhone(phoneNumber.trim());
            staff.setAddress(homeAddress.trim());
            staff.setEmail(email.trim().toLowerCase());
            staff.setRole(staffRole.trim());
            staff.setPassword(password.trim());
            staff.setStatus("Active");
            staff.setManagerId(managerId);
            staff.setProfilePicture(pictureBytes);
            staff.setStaffIdPrefix(staffIdPrefix);

            // Insert into database
            boolean success = staffDAO.registerStaff(staff);

            if (success) {
                response.sendRedirect("Login.jsp?signup=success");
            } else {
                request.setAttribute("error", "Sign up failed. Please try again.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
        }
    }

    private String determineStaffPrefix(String role) {
        if (role == null) return "STF";
        
        String upperRole = role.trim().toUpperCase();
        
        Map<String, String> roleToPrefixMap = Map.of(
            "FINANCE EXECUTIVE", "FinanceE",
            "SENIOR FINANCE MANAGER", "FinanceM",
            "FINANCE MANAGER", "FinanceM",
            "SENIOR MANAGER", "SeniorM",
            "MANAGER", "Manager",
            "OFFICER", "Officer",
            "CLERK", "Clerk",
            "ADMIN", "Admin",
            "ADMINISTRATOR", "Admin",
            "ASSISTANT", "Assistant",
            "SUPERVISOR", "Supervisor",
            "DIRECTOR", "Director"
        );

        return roleToPrefixMap.getOrDefault(upperRole, "Staff");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
