package Controller;

import DAO.StaffDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

@WebServlet("/ProfileImagesServlet")
public class ProfilePictureServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int staffID = 0;

        // 1) Try get staffID from URL param
        String staffIDParam = request.getParameter("staffID");
        if (staffIDParam != null && !staffIDParam.trim().isEmpty()) {
            try {
                staffID = Integer.parseInt(staffIDParam.trim());
            } catch (NumberFormatException e) {
                staffID = 0;
            }
        }

        // 2) If no param, try get from session
        if (staffID == 0) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("staffID") != null) {
                try {
                    staffID = (int) session.getAttribute("staffID");
                } catch (Exception ignored) {}
            }
        }

        // If still no staffID, return default image (or 404)
        if (staffID == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing staffID");
            return;
        }

        StaffDAO dao = new StaffDAO();

        try {
            byte[] imgBytes = dao.getStaffPictureById(staffID);

            if (imgBytes == null || imgBytes.length == 0) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
                return;
            }

            // Detect image type by magic number (JPEG/PNG)
            String contentType = detectImageType(imgBytes);
            response.setContentType(contentType);

            // Optional cache headers
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            response.setContentLength(imgBytes.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(imgBytes);
                out.flush();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

    private String detectImageType(byte[] bytes) {
        // PNG magic: 89 50 4E 47
        if (bytes.length >= 4
                && (bytes[0] & 0xFF) == 0x89
                && (bytes[1] & 0xFF) == 0x50
                && (bytes[2] & 0xFF) == 0x4E
                && (bytes[3] & 0xFF) == 0x47) {
            return "image/png";
        }

        // JPEG magic: FF D8
        if (bytes.length >= 2
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8) {
            return "image/jpeg";
        }

        // fallback
        return "application/octet-stream";
    }
}