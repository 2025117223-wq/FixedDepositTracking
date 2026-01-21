package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * LoginServlet - ALIGNED with YOUR Staff model and DAO
 * For Tomcat 10+ (Jakarta EE)
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;
    
    // Constant for session timeout (30 minutes)
    private static final int SESSION_TIMEOUT = 1800;

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
            Staff staff = staffDAO.login(emailClean, passwordClean);

            if (staff == null) {
                System.err.println("❌ Login failed: Invalid credentials");
                System.out.println("========================================");
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // Check if account is active (YOUR model uses getStatus())
            if (staff.getStatus() != null && !"Active".equalsIgnoreCase(staff.getStatus())) {
                System.err.println("❌ Login blocked: Account is not active");
                System.out.println("   Staff ID: " + staff.getStaffId());
                System.out.println("   Status: " + staff.getStatus());
                System.out.println("========================================");
                
                request.setAttribute("error", "Your account is not active. Please contact admin.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // Create session - USING YOUR MODEL FIELD NAMES
            HttpSession session = request.getSession(true);
            
            // Set session timeout to 30 minutes (SESSION_TIMEOUT constant)
            session.setMaxInactiveInterval(SESSION_TIMEOUT);
            
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
            System.out.println("🔑 SESSION DETAILS:");
            System.out.println("   Session ID: " + session.getId());
            System.out.println("   Session is new: " + session.isNew());
            System.out.println("   Session timeout: " + session.getMaxInactiveInterval() + " seconds");
            System.out.println("========================================");
            System.out.println("🔑 SESSION ATTRIBUTES VERIFIED:");
            System.out.println("   staffId = " + session.getAttribute("staffId"));
            System.out.println("   staffName = " + session.getAttribute("staffName"));
            System.out.println("   staffRole = " + session.getAttribute("staffRole"));
            System.out.println("========================================");

            // Redirect to Dashboard
            response.sendRedirect("Dashboard.jsp");

        } catch (Exception e) {
            System.err.println("❌ Exception during login: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            
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
