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

        String fullName = request.getParameter("fullName");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String homeAddress = request.getParameter("homeAddress");
        String staffRole = request.getParameter("staffRole");

        Part profilePart = request.getPart("profilePicture");

        if (isBlank(fullName) || isBlank(phoneNumber) || isBlank(email)
                || isBlank(password) || isBlank(homeAddress) || isBlank(staffRole)
                || profilePart == null || profilePart.getSize() == 0) {

            request.setAttribute("error", "Please fill in all required fields.");
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            return;
        }

        String contentType = profilePart.getContentType();
        if (contentType == null || !(contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/png"))) {

            request.setAttribute("error", "Profile picture must be JPEG or PNG.");
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            return;
        }

        // ✅ Convert image to byte[] for PostgreSQL BYTEA
        byte[] picBytes;
        try (InputStream is = profilePart.getInputStream()) {
            picBytes = is.readAllBytes(); // JDK 21 ok
        }

        if (picBytes == null || picBytes.length == 0) {
            request.setAttribute("error", "Profile picture is required.");
            request.getRequestDispatcher("SignUp.jsp").forward(request, response);
            return;
        }

        Staff staff = new Staff();
        staff.setStaffName(fullName.trim());
        staff.setStaffPhone(phoneNumber.trim());
        staff.setStaffAddress(homeAddress.trim());
        staff.setStaffEmail(email.trim().toLowerCase());
        staff.setStaffRole(staffRole.trim());
        staff.setPassword(PasswordUtil.processPassword(password.trim()));
        staff.setStaffPicture(picBytes); // ✅ byte[] setter

        StaffDAO dao = new StaffDAO();

        try {
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
