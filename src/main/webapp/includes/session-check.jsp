<%@ page import="com.fd.dao.StaffDAO" %>
<%@ page import="com.fd.model.Staff" %>

<%
    // ============================================
    // SESSION PROTECTION & USER DATA LOADING
    // Include this file at the top of protected pages
    // ============================================
    
    // Get logged-in user ID from session
    Integer loggedInStaffId = (Integer) session.getAttribute("staffId");
    
    // Redirect to login if not logged in
    if (loggedInStaffId == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    
    // Load user data from database
    StaffDAO staffDAO = new StaffDAO();
    Staff currentUser = staffDAO.getStaffById(loggedInStaffId);
    
    if (currentUser == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    
    // Prepare display data - Available for use in any page
    String userName = currentUser.getName() != null ? currentUser.getName() : "";
    String userRole = currentUser.getRole() != null ? currentUser.getRole() : "";
    String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "";
    String userStaffId = String.valueOf(currentUser.getStaffId());
    String userStatus = currentUser.getStatus() != null ? currentUser.getStatus() : "";
%>
