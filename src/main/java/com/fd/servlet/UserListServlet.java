package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/UserListServlet")
public class UserListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;

    public void init() {
        staffDAO = new StaffDAO();
        System.out.println("✅ UserListServlet initialized");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("🔍 UserListServlet - doGet called");
        System.out.println("========================================");
        
        List<Staff> staffList = staffDAO.getAllStaff();
        
        System.out.println("📊 Retrieved " + staffList.size() + " staff from database");
        
        if (staffList.isEmpty()) {
            System.err.println("⚠️ WARNING: No staff found in database!");
            System.err.println("   Check:");
            System.err.println("   1. Database connection");
            System.err.println("   2. Table name: FD.STAFF");
            System.err.println("   3. Data exists in table");
        }
        
        // Convert to JSON
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < staffList.size(); i++) {
            Staff s = staffList.get(i);
            
            // Debug first staff
            if (i == 0) {
                System.out.println("📋 First staff:");
                System.out.println("   ID: " + s.getStaffId());
                System.out.println("   Name: " + s.getName());
                System.out.println("   Role: " + s.getRole());
                System.out.println("   Status: " + s.getStatus());
            }
            
            jsonBuilder.append("{");
            jsonBuilder.append("\"staffId\":\"").append(s.getStaffId()).append("\",");
            jsonBuilder.append("\"name\":\"").append(escape(s.getName())).append("\",");
            jsonBuilder.append("\"role\":\"").append(escape(s.getRole())).append("\",");
            jsonBuilder.append("\"status\":\"").append(escape(s.getStatus())).append("\",");
            jsonBuilder.append("\"manageBy\":\"").append(escape(s.getManagerName())).append("\",");
            jsonBuilder.append("\"email\":\"").append(escape(s.getEmail())).append("\",");
            jsonBuilder.append("\"phone\":\"").append(escape(s.getPhone())).append("\",");
            jsonBuilder.append("\"address\":\"").append(escape(s.getAddress())).append("\",");
            jsonBuilder.append("\"reason\":").append(s.getReason() != null ? "\"" + escape(s.getReason()) + "\"" : "null");
            jsonBuilder.append("}");
            if (i < staffList.size() - 1) jsonBuilder.append(",");
        }
        jsonBuilder.append("]");

        String jsonData = jsonBuilder.toString();
        System.out.println("📤 JSON Data length: " + jsonData.length() + " characters");
        System.out.println("📤 JSON Preview: " + (jsonData.length() > 200 ? jsonData.substring(0, 200) + "..." : jsonData));
        
        request.setAttribute("userJsonData", jsonData);
        
        System.out.println("✅ Forwarding to UserList.jsp");
        System.out.println("========================================");
        
        request.getRequestDispatcher("UserList.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("🔍 UserListServlet - doPost (UPDATE)");
        System.out.println("========================================");
        
        try {
            String idStr = request.getParameter("editStaffId");
            System.out.println("📝 Received Update for Staff ID: " + idStr);

            if(idStr == null || idStr.isEmpty()) {
                System.err.println("❌ ERROR: Staff ID is missing!");
                throw new Exception("Staff ID is missing. Check JSP form field names.");
            }

            int staffId = Integer.parseInt(idStr);
            String role = request.getParameter("editRole");
            String status = request.getParameter("editStatus");
            String reason = request.getParameter("editReason");
            
            System.out.println("   Role: " + role);
            System.out.println("   Status: " + status);
            System.out.println("   Reason: " + reason);

            Staff staff = new Staff();
            staff.setStaffId(staffId);
            staff.setRole(role);
            staff.setStatus(status);
            staff.setReason(reason);

            boolean success = staffDAO.updateStaff(staff);
            
            if(success) {
                System.out.println("✅ Update Successful for Staff ID " + staffId);
            } else {
                System.err.println("❌ Update Failed for Staff ID " + staffId);
            }

        } catch (Exception e) {
            System.err.println("❌ Exception in doPost: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("🔄 Redirecting to UserListServlet (refresh)");
        System.out.println("========================================");

        response.sendRedirect("UserListServlet");
    }

    private String escape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
