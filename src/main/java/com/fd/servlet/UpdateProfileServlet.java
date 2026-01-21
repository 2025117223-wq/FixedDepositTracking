package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;
import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * UpdateProfileServlet - Updates user profile information
 * For Tomcat 10+ (Jakarta EE)
 */
@WebServlet("/UpdateProfileServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,  // 1MB
    maxFileSize = 5 * 1024 * 1024,    // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class UpdateProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;

    @Override
    public void init() {
        staffDAO = new StaffDAO();
        System.out.println("========================================");
        System.out.println("✅ UpdateProfileServlet INITIALIZED");
        System.out.println("========================================");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("========================================");
        System.out.println("📝 UpdateProfileServlet - POST Request");
        System.out.println("========================================");
        
        HttpSession session = request.getSession();
        
        try {
            // Get form parameters
            String staffIdStr = request.getParameter("staffId");
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String password = request.getParameter("password");
            
            System.out.println("📋 Parameters:");
            System.out.println("   Staff ID: " + staffIdStr);
            System.out.println("   Name: " + name);
            System.out.println("   Phone: " + phone);
            System.out.println("   Password: " + (password != null && !password.isEmpty() ? "***" : "(not changing)"));
            
            // Validate staff ID
            if (staffIdStr == null || staffIdStr.isEmpty()) {
                System.err.println("❌ ERROR: Staff ID missing");
                response.sendRedirect("Profile.jsp?error=missing_id");
                return;
            }
            
            int staffId = Integer.parseInt(staffIdStr);
            
            // Get current user data
            Staff staff = staffDAO.getStaffById(staffId);
            
            if (staff == null) {
                System.err.println("❌ ERROR: Staff not found with ID " + staffId);
                response.sendRedirect("Profile.jsp?error=not_found");
                return;
            }
            
            // Update fields
            staff.setName(name);
            staff.setPhone(phone);
            staff.setAddress(address);
            
            // Only update password if provided
            if (password != null && !password.isEmpty()) {
                staff.setPassword(password);
                System.out.println("   🔒 Password will be updated");
            } else {
                System.out.println("   🔒 Password unchanged");
            }
            
            // Handle profile picture upload
            Part profilePicturePart = request.getPart("profilePicture");
            if (profilePicturePart != null && profilePicturePart.getSize() > 0) {
                // Validate file type
                String contentType = profilePicturePart.getContentType();
                if (contentType != null && 
                    (contentType.equalsIgnoreCase("image/jpeg") || 
                     contentType.equalsIgnoreCase("image/jpg") || 
                     contentType.equalsIgnoreCase("image/png"))) {
                    
                    // Read picture bytes
                    try (InputStream inputStream = profilePicturePart.getInputStream()) {
                        byte[] pictureBytes = inputStream.readAllBytes();
                        staff.setProfilePicture(pictureBytes);
                        System.out.println("   📷 Profile picture will be updated (" + pictureBytes.length + " bytes)");
                    }
                } else {
                    System.out.println("   ⚠️ Invalid file type, profile picture not updated");
                }
            } else {
                System.out.println("   📷 No new profile picture uploaded");
            }
            
            // Update database
            System.out.println("💾 Updating profile in database...");
            boolean success = staffDAO.updateStaffProfile(staff);
            
            if (success) {
                System.out.println("✅ PROFILE UPDATE SUCCESSFUL");
                System.out.println("   Staff ID: " + staffId);
                System.out.println("   Name: " + name);
                System.out.println("========================================");
                
                // Update session with new name
                session.setAttribute("staffName", name);
                
                response.sendRedirect("Profile.jsp?success=true");
            } else {
                System.err.println("❌ PROFILE UPDATE FAILED");
                System.out.println("========================================");
                response.sendRedirect("Profile.jsp?error=update_failed");
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid Staff ID format: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            response.sendRedirect("Profile.jsp?error=invalid_id");
            
        } catch (Exception e) {
            System.err.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            response.sendRedirect("Profile.jsp?error=exception");
        }
    }
}
