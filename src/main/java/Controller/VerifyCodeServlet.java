package com.fd.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * VerifyCodeServlet - Step 2: Verify OTP code
 */
@WebServlet("/VerifyCodeServlet")
public class VerifyCodeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        System.out.println("========================================");
        System.out.println("🔐 VerifyCodeServlet - OTP Verification");
        System.out.println("========================================");

        HttpSession session = request.getSession(false);

        // Check if session exists
        if (session == null) {
            System.err.println("❌ No session found");
            System.out.println("========================================");
            response.sendRedirect("ForgotPassword.jsp");
            return;
        }

        String sessionOtp = (String) session.getAttribute("fp_otp");
        Long expiry = (Long) session.getAttribute("fp_expiry");

        if (sessionOtp == null || expiry == null) {
            System.err.println("❌ Session data missing");
            System.out.println("========================================");
            request.setAttribute("error", "Session expired. Please try again.");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }

        // Check if OTP expired
        if (System.currentTimeMillis() > expiry) {
            System.err.println("❌ OTP expired");
            System.out.println("========================================");
            session.invalidate();
            request.setAttribute("error", "Verification code expired. Please request a new one.");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }

        String userOtp = request.getParameter("otp");

        if (userOtp == null || userOtp.trim().isEmpty()) {
            System.err.println("❌ No OTP provided");
            request.setAttribute("error", "Please enter the verification code.");
            request.getRequestDispatcher("VerifyCode.jsp").forward(request, response);
            return;
        }

        userOtp = userOtp.trim();
        System.out.println("🔑 Entered OTP: " + userOtp);
        System.out.println("🔑 Expected OTP: " + sessionOtp);

        // Verify OTP
        if (!userOtp.equals(sessionOtp)) {
            System.err.println("❌ Invalid OTP");
            System.out.println("========================================");
            request.setAttribute("error", "Invalid verification code. Please try again.");
            request.getRequestDispatcher("VerifyCode.jsp").forward(request, response);
            return;
        }

        // OTP verified successfully
        session.setAttribute("fp_verified", true);
        
        System.out.println("✅ OTP VERIFIED SUCCESSFULLY");
        System.out.println("   Redirecting to ResetPassword.jsp");
        System.out.println("========================================");

        // Redirect to reset password page
        response.sendRedirect("ResetPassword.jsp?verified=true");
    }
}