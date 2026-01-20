package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;
import com.fd.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/UpdateUserServlet")
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 5,      // 5MB
    maxRequestSize = 1024 * 1024 * 10   // 10MB
)
public class UpdateUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;

    @Override
    public void init() {
        staffDAO = new StaffDAO();
        System.out.println("✅ UpdateUserServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        System.out.println("========================================");
        System.out.println("🔍 UpdateUserServlet - POST Request Received");
        System.out.println("========================================");
        
        try {
            // Get form parameters
            String staffIdStr = request.getParameter("editStaffId");
            String role = request.getParameter("editRole");
            String status = request.getParameter("editStatus");
            String reasonText = request.getParameter("editReason");
            String password = request.getParameter("editPassword");  // Password update (if provided)
            
            System.out.println("📝 Parameters:");
            System.out.println("   Staff ID: " + staffIdStr);
            System.out.println("   Role: " + role);
            System.out.println("   Status: " + status);
            System.out.println("   Reason: " + reasonText);
            System.out.println("   Password: " + (password != null && !password.isEmpty() ? "***" : "(not changing)"));
            
            // Handle file upload if present
            String uploadedFilePath = null;
            try {
                Part filePart = request.getPart("editReasonFile");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = getFileName(filePart);
                    System.out.println("   File: " + fileName + " (" + filePart.getSize() + " bytes)");

                    // Validate file type (only accept certain file types, e.g., .txt, .pdf, etc.)
                    String contentType = filePart.getContentType();
                    if (!contentType.equals("application/pdf") && !contentType.startsWith("image/")) {
                        System.err.println("❌ Invalid file type: " + contentType);
                        response.sendRedirect("UserList.jsp?error=invalid_file_type");
                        return;
                    }

                    // Save the file to a directory
                    String uploadDir = getServletContext().getRealPath("/uploads");
                    File uploadDirectory = new File(uploadDir);
                    if (!uploadDirectory.exists()) {
                        uploadDirectory.mkdir();
                    }
                    
                    uploadedFilePath = uploadDir + File.separator + fileName;
                    filePart.write(uploadedFilePath);  // Save file to disk
                    reasonText = uploadedFilePath;  // Save file path to reason field
                }
            } catch (Exception e) {
                System.out.println("   No file uploaded");
            }
            
            // Validate staff ID
            if (staffIdStr == null || staffIdStr.isEmpty()) {
                System.err.println("❌ ERROR: Missing Staff ID");
                response.sendRedirect("UserList.jsp?error=missing_id");
                return;
            }
            
            int staffId = Integer.parseInt(staffIdStr);
            
            // Create Staff object
            Staff staff = new Staff();
            staff.setStaffId(staffId);
            staff.setRole(role);
            staff.setStatus(status);
            
            // Handle reason (use uploaded file path if the status is inactive)
            if ("Inactive".equalsIgnoreCase(status)) {
                staff.setReason(reasonText != null && !reasonText.isEmpty() ? reasonText : "No reason provided");
            } else {
                staff.setReason(null);
            }
            
            // If a new password is provided, hash it before saving
            if (password != null && !password.isEmpty()) {
                String hashedPassword = PasswordUtil.processPassword(password);  // Hash the new password
                staff.setPassword(hashedPassword);  // Set the hashed password
                System.out.println("   🔒 Password will be updated");
            }
            
            // Handle profile picture upload
            try {
                Part picturePart = request.getPart("editPicture");
                if (picturePart != null && picturePart.getSize() > 0) {
                    try (InputStream pictureInputStream = picturePart.getInputStream()) {
                        byte[] pictureBytes = new byte[pictureInputStream.available()];
                        pictureInputStream.read(pictureBytes);  // Read the picture into a byte array
                        staff.setProfilePicture(pictureBytes);  // Set the profile picture as byte array
                        System.out.println("   📷 Profile picture uploaded (" + pictureBytes.length + " bytes)");
                    }
                }
            } catch (Exception e) {
                System.out.println("   No profile picture uploaded");
            }
            
            // Update database
            System.out.println("💾 Updating database...");
            boolean success = staffDAO.updateStaff(staff);
            
            if (success) {
                System.out.println("✅ UPDATE SUCCESSFUL - Staff ID: " + staffId);
                System.out.println("   Role: " + role);
                System.out.println("   Status: " + status);
                System.out.println("========================================");
                response.sendRedirect("UserList.jsp?success=true");
            } else {
                System.err.println("❌ UPDATE FAILED - Staff ID: " + staffId);
                System.out.println("========================================");
                response.sendRedirect("UserList.jsp?error=update_failed");
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid Staff ID: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            response.sendRedirect("UserList.jsp?error=invalid_id");
            
        } catch (Exception e) {
            System.err.println("❌ Exception: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            response.sendRedirect("UserList.jsp?error=exception");
        }
    }
    
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) return "uploaded_file";
        
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "uploaded_file";
    }
}
