package Controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

        // ✅ Validate empty
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
            return;
        }

        // ✅ Validate match
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

        // ✅ Check new password != old password (compare hash)
        String newHash = sha256(newPassword);

        String oldHash = getStaffPasswordHash(staffId);
        if (oldHash == null) {
            request.setAttribute("error", "User not found. Please try again.");
            request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
            return;
        }

        if (newHash.equals(oldHash)) {
            request.setAttribute("error", "New password cannot be the same as the old password.");
            request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
            return;
        }

        // ✅ Update password (store hash)
        boolean updated = updateStaffPasswordHash(staffId, newHash);

        if (!updated) {
            request.setAttribute("error", "Failed to reset password. Please try again.");
            request.getRequestDispatcher("ResetPassword.jsp").forward(request, response);
            return;
        }

        session.invalidate();
        response.sendRedirect("Login.jsp?reset=success");
    }

    private String getStaffPasswordHash(int staffId) {
        String sql = "SELECT password FROM Staff WHERE staffID=?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password"); // assumed stored hash
                }
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean updateStaffPasswordHash(int staffId, String newHash) {
        String sql = "UPDATE Staff SET password=? WHERE staffID=?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
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
