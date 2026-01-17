package Controller;

import Bean.Staff;
import DAO.StaffDAO;
import Util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

@WebServlet("/SignUpServlet")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class SignUpServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== Read params =====
        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String homeAddress = request.getParameter("homeAddress");
        String staffRole = request.getParameter("staffRole");
        String managerIDStr = request.getParameter("managerID"); // NEW

        Part profilePart = request.getPart("profilePicture");

        // ===== Basic required validation =====
        if (isBlank(fullName) || isBlank(phoneNumber) || isBlank(email)
                || isBlank(password) || isBlank(confirmPassword)
                || isBlank(homeAddress) || isBlank(staffRole)
                || isBlank(managerIDStr)
                || profilePart == null || profilePart.getSize() == 0) {

            request.setAttribute("error", "Please fill in all required fields.");
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            return;
        }

        // ===== Password validation =====
        if (password.trim().length() < 8) {
            request.setAttribute("error", "Password must be at least 8 characters.");
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            return;
        }

        // ===== ManagerID parse =====
        Integer managerID;
        try {
            managerID = Integer.valueOf(managerIDStr);
            if (managerID <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            request.setAttribute("error", "Invalid manager selected.");
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            return;
        }

        // ===== Profile picture validation =====
        String contentType = profilePart.getContentType();
        if (contentType == null ||
                !(contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/jpg")
                        || contentType.equalsIgnoreCase("image/png"))) {

            request.setAttribute("error", "Profile picture must be JPEG or PNG.");
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            return;
        }

        // ===== Build Staff bean =====
        Staff staff = new Staff();
        staff.setStaffName(fullName.trim());
        staff.setStaffPhone(phoneNumber.trim());
        staff.setStaffAddress(homeAddress.trim());
        staff.setStaffEmail(email.trim().toLowerCase());
        staff.setStaffRole(staffRole.trim());
        staff.setManagerID(managerID);              // NEW
        staff.setStaffStatus("ACTIVE");             // optional default
        staff.setPassword(PasswordUtil.processPassword(password.trim()));

        StaffDAO dao = new StaffDAO();

        try (InputStream pictureStream = profilePart.getInputStream()) {
            staff.setStaffPicture(pictureStream);

            // ===== Duplicate email check =====
            if (dao.isEmailExists(staff.getStaffEmail())) {
                request.setAttribute("error", "Email already registered.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
                return;
            }

            boolean ok = dao.insertStaff(staff);

            if (ok) {
                response.sendRedirect("Login.jsp?signup=success");
            } else {
                request.setAttribute("error", "Sign up failed.");
                request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}