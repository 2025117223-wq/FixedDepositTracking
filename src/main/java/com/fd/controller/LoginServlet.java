package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;
import com.fd.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;

    @Override
    public void init() {
        staffDAO = new StaffDAO();
        System.out.println("========================================");
        System.out.println("✅ LoginServlet INITIALIZED");
        System.out.println("========================================");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("========================================");
        System.out.println("🔐 LoginServlet - Login Attempt");
        System.out.println("========================================");

        // Get form parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Keep email in form (for user convenience)
        if (email != null) {
            request.setAttribute("emailValue", email.trim());
        }

        System.out.println("📧 Email: " + (email != null ? email.trim() : "null"));

        // Basic validation
        if (isBlank(email) || isBlank(password)) {
            System.err.println("❌ Validation failed: Email or password is blank");
            request.setAttribute("error", "Please enter email and password.");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
            return;
        }

        String emailClean = email.trim().toLowerCase();
        String passwordClean = password.trim();

        try {
            // Login using YOUR DAO (which expects email and password)
            System.out.println("🔍 Attempting login...");
            Staff staff = staffDAO.login(emailClean);

            if (staff == null) {
                System.err.println("❌ Login failed: Invalid credentials");
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // Verify password using BCrypt
            if (!PasswordUtil.checkPassword(passwordClean, staff.getPassword())) {
                System.err.println("❌ Login failed: Invalid password");
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // Check if account is active (YOUR model uses getStatus())
            if (staff.getStatus() != null && !"Active".equalsIgnoreCase(staff.getStatus())) {
                System.err.println("❌ Login blocked: Account is not active");
                request.setAttribute("error", "Your account is not active. Please contact admin.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // Create session - USING YOUR MODEL FIELD NAMES
            HttpSession session = request.getSession(true);
            
            // Set session timeout to 30 minutes (1800 seconds)
            session.setMaxInactiveInterval(1800);
            
            session.setAttribute("loggedStaff", staff);                    // Full staff object
            session.setAttribute("staffId", staff.getStaffId());           // YOUR MODEL: getStaffId()
            session.setAttribute("staffName", staff.getName());            // YOUR MODEL: getName()
            session.setAttribute("staffEmail", staff.getEmail());          // YOUR MODEL: getEmail()
            session.setAttribute("staffRole", staff.getRole());            // YOUR MODEL: getRole()
            session.setAttribute("staffStatus", staff.getStatus());        // YOUR MODEL: getStatus()
            session.setAttribute("managerId", staff.getManagerId());       // YOUR MODEL: getManagerId()

            System.out.println("✅ LOGIN SUCCESSFUL");
            System.out.println("   Staff ID: " + staff.getStaffId());
            System.out.println("   Name: " + staff.getName());
            System.out.println("   Email: " + staff.getEmail());
            System.out.println("   Role: " + staff.getRole());
            System.out.println("   Status: " + staff.getStatus());
            System.out.println("   Manager ID: " + staff.getManagerId());
            System.out.println("========================================");

            // Redirect to Dashboard
            response.sendRedirect("Dashboard.jsp");

        } catch (Exception e) {
            System.err.println("❌ Exception during login: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
    }

    /**
     * Check if string is blank (null or empty)
     */
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
