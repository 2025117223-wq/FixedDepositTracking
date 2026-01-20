package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;


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
        System.out.println("========================================");
        System.out.println("✅ UpdateUserServlet INITIALIZED (Tomcat 10+)");
        System.out.println("========================================");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h1>✅ UpdateUserServlet is WORKING!</h1>");
        response.getWriter().println("<p>Servlet is properly deployed (Tomcat 10+)</p>");
        response.getWriter().println("<p>Jakarta EE namespace: CORRECT</p>");
        response.getWriter().println("<p><a href='UserList.jsp'>Go to User List</a></p>");
        response.getWriter().println("</body></html>");
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
            
            System.out.println("📝 Parameters:");
            System.out.println("   Staff ID: " + staffIdStr);
            System.out.println("   Role: " + role);
            System.out.println("   Status: " + status);
            System.out.println("   Reason: " + reasonText);
            
            // Handle file upload if present
            try {
                Part filePart = request.getPart("editReasonFile");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = getFileName(filePart);
                    System.out.println("   File: " + fileName + " (" + filePart.getSize() + " bytes)");
                    reasonText = fileName;
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
            
            // Handle reason
            if ("Inactive".equalsIgnoreCase(status)) {
                staff.setReason(reasonText != null && !reasonText.isEmpty() ? reasonText : "No reason provided");
            } else {
                staff.setReason(null);
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
