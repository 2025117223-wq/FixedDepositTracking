package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import Util.DBConn;
import Util.EmailUtil;

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email is required.");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();

        // 1) Check staff email exists & ACTIVE
        Integer staffId = getActiveStaffIdByEmail(email);

        if (staffId == null) {
            request.setAttribute("error", "Email not found or staff inactive.");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }

        // 2) Generate OTP (6 digits)
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        // 3) Store OTP in session (5 minutes expiry)
        HttpSession session = request.getSession();
        session.setAttribute("fp_staffId", staffId);
        session.setAttribute("fp_email", email);
        session.setAttribute("fp_otp", otp);
        session.setAttribute("fp_expiry", System.currentTimeMillis() + (5 * 60 * 1000));
        session.setAttribute("fp_verified", false);

        // 4) Send OTP email
        try {
            EmailUtil.sendEmail(
                    email,
                    "Fixed Deposit Tracking System - Password Reset OTP",
                    "Your verification code is: " + otp
                            + "\n\nThis code will expire in 5 minutes."
            );
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to send verification email. Please try again.");
            request.getRequestDispatcher("ForgotPassword.jsp").forward(request, response);
            return;
        }

        // 5) Redirect to verify page
        response.sendRedirect("VerifyCode.jsp");
    }

    private Integer getActiveStaffIdByEmail(String email) {
        // ✅ PostgreSQL default: lowercase table/columns
        String sql = "SELECT staffid FROM staff WHERE staffemail = ? AND staffstatus = 'ACTIVE'";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("staffid");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
