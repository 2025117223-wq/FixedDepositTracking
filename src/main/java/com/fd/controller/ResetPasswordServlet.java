package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * ResetPasswordServlet - Step 3: Reset password
 * ALIGNED with YOUR Oracle database
 */
@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        System.out.println("========================================");
        System.out.println("🔐 ResetPasswordServlet - Password Reset");
        System.out.println("========================================");

        HttpSession session = request.getSession(false);

        // Must be verified first (from VerifyCode step)
        if (session == null || session.getAttribute("fp_verified") == null
                || !(Boolean) session.getAttribute("fp_verified")) {
            System.err.println("❌ Not verified");
            System.out.println("========================================");
            response.sendRedirect("ForgotPassword.jsp");
            return;
        }

        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm");

        if (newPassword == null) newPassword = "";
        if (confirmPassword == null) confirmPassword = "";

        newPassword = newPassword.trim();
        confirmPassword = confirmPassword.trim();

        System.out.println("📝 New password length: " + newPassword.length());

        // Validate empty
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            System.err.println("❌ Empty password fields");
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        // Validate match
        if (!newPassword.equals(confirmPassword)) {
            System.err.println("❌ Passwords don't match");
            request.setAttribute("error", "Password and Confirm Password do not match!");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        // Validate length
        if (newPassword.length() < 6) {
            System.err.println("❌ Password too short");
            request.setAttribute("error", "Password must be at least 6 characters long.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        Integer staffId = (Integer) session.getAttribute("fp_staffId");
        if (staffId == null) {
            System.err.println("❌ No staff ID in session");
            System.out.println("========================================");
            response.sendRedirect("ForgotPassword.jsp");
            return;
        }

        System.out.println("👤 Staff ID: " + staffId);

        StaffDAO staffDAO = new StaffDAO();

        // Get current password (to prevent using same password)
        String oldPassword = staffDAO.getPasswordByStaffId(staffId);
        
        if (oldPassword == null) {
            System.err.println("❌ Staff not found");
            request.setAttribute("error", "User not found. Please try again.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        // Check if new password is same as old password
        if (PasswordUtil.checkPassword(newPassword, oldPassword)) {
            System.err.println("❌ Same as old password");
            request.setAttribute("error", "New password cannot be the same as the old password.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        // Hash the new password before saving it
        String hashedPassword = PasswordUtil.processPassword(newPassword);

        // Update password
        System.out.println("💾 Updating password...");
        boolean updated = staffDAO.updatePasswordByStaffId(staffId, hashedPassword);

        if (!updated) {
            System.err.println("❌ Failed to update password");
            System.out.println("========================================");
            request.setAttribute("error", "Failed to reset password. Please try again.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        System.out.println("✅ PASSWORD RESET SUCCESSFUL");
        System.out.println("   Staff ID: " + staffId);
        System.out.println("   Clearing session and redirecting to login");
        System.out.println("========================================");

        // Clear session data
        session.invalidate();

        // Redirect to login with success message
        response.sendRedirect("Login.jsp?reset=success");
    }
}
