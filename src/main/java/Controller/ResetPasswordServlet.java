package Controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import Util.DBConn;

@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("fp_verified") == null
                || !(Boolean) session.getAttribute("fp_verified")) {
            response.sendRedirect("ForgotPassword.jsp");
            return;
        }

        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm");

        if (newPassword == null) newPassword = "";
        if (confirmPassword == null) confirmPassword = "";

        newPassword = newPassword.trim();
        confirmPassword = confirmPassword.trim();

        // ✅ Validate
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Password and Confirm Password do not match!");
            request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
            return;
        }

        Integer staffId = (Integer) session.getAttribute("fp_staffId");
        if (staffId == null) {
            response.sendRedirect("ForgotPassword.jsp");
            return;
        }

        // ✅ Hash password (recommended)
        boolean updated = updateStaffPassword(staffId, newPassword);

        if (!updated) {
            request.setAttribute("error", "Failed to reset password. Please try again.");
            request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
            return;
        }


        session.invalidate();


        response.sendRedirect("Login.jsp?reset=success");
    }

    private boolean updateStaffPassword(int staffId, String newPassword) {
        String sql = "UPDATE Staff SET password=? WHERE staffID=?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, staffId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
