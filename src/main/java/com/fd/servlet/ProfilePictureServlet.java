package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

/**
 * ProfilePictureServlet - Serves staff profile pictures from database
 * Usage: <img src="ProfilePictureServlet?staffId=123">
 */
@WebServlet("/ProfilePictureServlet")
public class ProfilePictureServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;

    @Override
    public void init() {
        staffDAO = new StaffDAO();
        System.out.println("========================================");
        System.out.println("✅ ProfilePictureServlet INITIALIZED");
        System.out.println("========================================");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String staffIdStr = request.getParameter("staffId");
        
        if (staffIdStr == null || staffIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Staff ID is required");
            return;
        }
        
        try {
            int staffId = Integer.parseInt(staffIdStr);
            
            // Get staff from database
            Staff staff = staffDAO.getStaffById(staffId);
            
            if (staff == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Staff not found");
                return;
            }
            
            byte[] profilePicture = staff.getProfilePicture();
            
            if (profilePicture == null || profilePicture.length == 0) {
                // No profile picture - return 404 so the onerror handler in JSP works
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "No profile picture");
                return;
            }
            
            // Set content type to image
            response.setContentType("image/jpeg");
            response.setContentLength(profilePicture.length);
            
            // Write image bytes to response
            try (OutputStream out = response.getOutputStream()) {
                out.write(profilePicture);
                out.flush();
            }
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Staff ID");
        } catch (Exception e) {
            System.err.println("❌ Error serving profile picture: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading image");
        }
    }
}
