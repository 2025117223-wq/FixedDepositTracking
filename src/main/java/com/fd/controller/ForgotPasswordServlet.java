package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.security.SecureRandom;
import jakarta.mail.MessagingException;  // Import added for MessagingException

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        System.out.println("========================================");
        System.out.println("🔐 ForgotPasswordServlet - Password Reset Request");
        System.out.println("========================================");
        
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            System.err.println("❌ Email is empty");
            request.setAttribute("error", "Email is required.");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();
        System.out.println("📧 Email: " + email);

        // Check if email exists and staff is Active
        StaffDAO staffDAO = new StaffDAO();
        Long staffId = staffDAO.getStaffIdByEmailAndStatus(email, "Active");  // Changed to Long

        if (staffId == null) {
            System.err.println("❌ Email not found or staff not active");
            System.out.println("========================================");
            request.setAttribute("error", "Email not found or account is not active.");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }

        System.out.println("✅ Staff found - ID: " + staffId);

        // Generate 6-digit OTP using SecureRandom (for better security)
        SecureRandom random = new SecureRandom();
        String otp = String.format("%06d", random.nextInt(999999));  // 6-digit OTP
        System.out.println("🔑 OTP Generated: " + otp);

        // Store OTP in session (5 minutes expiry)
        HttpSession session = request.getSession();
        session.setAttribute("fp_staffId", staffId);
        session.setAttribute("fp_email", email);
        session.setAttribute("fp_otp", otp);
        session.setAttribute("fp_expiry", System.currentTimeMillis() + (5 * 60 * 1000)); // 5 minutes
        session.setAttribute("fp_verified", false);

        System.out.println("💾 Session data stored");
        System.out.println("   Expiry: 5 minutes from now");

        // Send OTP email
        try {
            System.out.println("📧 Sending OTP email...");
            EmailUtil.sendEmail(
                    email,
                    "Fixed Deposit Tracking System - Password Reset OTP",
                    "Your verification code is: " + otp + "\n\n" +
                    "This code will expire in 5 minutes.\n\n" +
                    "If you did not request this, please ignore this email."
            );
            System.out.println("✅ Email sent successfully");
        } catch (MessagingException e) {  // Catch MessagingException
            System.err.println("❌ Failed to send email");
            e.printStackTrace();
            System.out.println("========================================");
            request.setAttribute("error", "Unable to send verification email. Please try again.");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }

        System.out.println("✅ PASSWORD RESET REQUEST SUCCESSFUL");
        System.out.println("   Redirecting to VerifyCode.jsp");
        System.out.println("========================================");

        // Redirect to verify page
        response.sendRedirect("VerifyCode.jsp?sent=true");
    }
}
