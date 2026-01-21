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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;
    
    // Constant for session timeout (30 minutes)
    private static final int SESSION_TIMEOUT = 1800;
    
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);

    @Override
    public void init() {
        staffDAO = new StaffDAO();
        logger.info("LoginServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        logger.info("Login attempt received");

        // Get form parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Keep email in form (for user convenience)
        if (email != null) {
            request.setAttribute("emailValue", email.trim());
        }

        logger.debug("Email: {}", email != null ? email.trim() : "null");

        // Basic validation
        if (isBlank(email) || isBlank(password)) {
            logger.error("Validation failed: Email or password is blank");
            request.setAttribute("error", "Please enter email and password.");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
            return;
        }

        String emailClean = email.trim().toLowerCase();
        String passwordClean = password.trim();

        try {
            // Login using YOUR DAO (which expects email and password)
            logger.info("Attempting login for email: {}", emailClean);
            Staff staff = staffDAO.login(emailClean, passwordClean);

            if (staff == null) {
                logger.error("Login failed: Invalid credentials");
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // Check if account is active
            if (staff.getStatus() != null && !"Active".equalsIgnoreCase(staff.getStatus())) {
                logger.error("Login blocked: Account is not active for staff ID: {}", staff.getStaffId());
                request.setAttribute("error", "Your account is not active. Please contact admin.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // Create session and set session attributes
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(SESSION_TIMEOUT);
            
            session.setAttribute("loggedStaff", staff);                    // Full staff object
            session.setAttribute("staffId", staff.getStaffId());           // Staff ID
            session.setAttribute("staffName", staff.getName());            // Staff Name
            session.setAttribute("staffEmail", staff.getEmail());          // Staff Email
            session.setAttribute("staffRole", staff.getRole());            // Staff Role
            session.setAttribute("staffStatus", staff.getStatus());        // Staff Status
            session.setAttribute("managerId", staff.getManagerId());       // Staff Manager ID

            logger.info("Login successful for staff ID: {}", staff.getStaffId());
            logger.debug("Session details: {}", session.getId());

            // Redirect to Dashboard
            response.sendRedirect("Dashboard.jsp");

        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage());
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
