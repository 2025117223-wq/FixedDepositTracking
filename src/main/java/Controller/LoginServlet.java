package Controller;

import Bean.Staff;
import DAO.StaffDAO;
import Util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter email and password.");
            request.getRequestDispatcher("Login.jsp").forward(request, response);
            return;
        }

        String emailClean = email.trim().toLowerCase();
        String processedPassword = PasswordUtil.processPassword(password.trim()); // plain password

        StaffDAO dao = new StaffDAO();

        try {
            Staff staff = dao.login(emailClean, processedPassword);

            if (staff == null) {
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            // Optional: block inactive account
            if (staff.getStaffStatus() != null && !"ACTIVE".equalsIgnoreCase(staff.getStaffStatus())) {
                request.setAttribute("error", "Your account is not active. Please contact admin.");
                request.getRequestDispatcher("Login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("loggedStaff", staff);
            session.setAttribute("staffID", staff.getStaffID());
            session.setAttribute("staffName", staff.getStaffName());
            session.setAttribute("staffRole", staff.getStaffRole());

            response.sendRedirect("Dashboard.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("Login.jsp").forward(request, response);
        }
    }
}