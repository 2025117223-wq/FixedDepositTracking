package com.fd.servlet;

import com.fd.dao.StaffDAO;
import com.fd.model.Staff;
import com.fd.util.PasswordUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/UserListServlet")
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 5,      // 5MB
    maxRequestSize = 1024 * 1024 * 10   // 10MB
)
public class UserListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StaffDAO staffDAO;

    public void init() {
        staffDAO = new StaffDAO();
        System.out.println("✅ UserListServlet initialized");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Staff> staffList = staffDAO.getAllStaff();
        
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (int i = 0; i < staffList.size(); i++) {
            Staff s = staffList.get(i);
            
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
        request.setAttribute("userJsonData", jsonData);
        
        request.getRequestDispatcher("UserList.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String idStr = request.getParameter("editStaffId");
            if(idStr == null || idStr.isEmpty()) {
                throw new Exception("Staff ID is missing.");
            }
            
            int staffId = Integer.parseInt(idStr);
            String role = request.getParameter("editRole");
            String status = request.getParameter("editStatus");
            String reason = request.getParameter("editReason");
            
            Staff staff = new Staff();
            staff.setStaffId(staffId);
            staff.setRole(role);
            staff.setStatus(status);
            staff.setReason(reason);

            boolean success = staffDAO.updateStaff(staff);
            if(success) {
                response.sendRedirect("UserListServlet?success=true");
            } else {
                response.sendRedirect("UserListServlet?error=update_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("UserListServlet?error=" + e.getMessage());
        }
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
