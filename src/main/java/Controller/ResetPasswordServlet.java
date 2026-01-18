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

@WebServlet("/ResetPasswordServlet")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Must be verified first (from VerifyCode step)
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

        // Validate empty
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        // Validate match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Password and Confirm Password do not match!");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        Integer staffId = (Integer) session.getAttribute("fp_staffId");
        if (staffId == null) {
            response.sendRedirect("ForgotPassword.jsp");
            return;
        }

        // Compare new password with old password (PLAIN TEXT)
        String oldPassword = getStaffPasswordPlain(staffId);
        if (oldPassword == null) {
            request.setAttribute("error", "User not found. Please try again.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        if (newPassword.equals(oldPassword)) {
            request.setAttribute("error", "New password cannot be the same as the old password.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        // Update password (store PLAIN TEXT)
        boolean updated = updateStaffPasswordPlain(staffId, newPassword);

        if (!updated) {
            request.setAttribute("error", "Failed to reset password. Please try again.");
            request.getRequestDispatcher("ResetPassword.jsp?verified=true").forward(request, response);
            return;
        }

        // Clear session data
        session.invalidate();
        response.sendRedirect("Login.jsp?reset=success");
    }

    private String getStaffPasswordPlain(int staffId) {
        String sql = "SELECT password FROM staff WHERE staffid = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean updateStaffPasswordPlain(int staffId, String newPassword) {
        String sql = "UPDATE staff SET password = ? WHERE staffid = ?";

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
}
