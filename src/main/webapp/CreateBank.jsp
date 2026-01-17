<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Bean.Staff" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    // =========================
    // SESSION PROTECTION
    // =========================
    Staff loggedStaff = (Staff) session.getAttribute("loggedStaff");
    if (loggedStaff == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    String staffName = loggedStaff.getStaffName();
    String staffRole = loggedStaff.getStaffRole();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Bank - Fixed Deposit Tracking System</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/CreateBank.css">
</head>
<body>

   <%@ include file="includes/sidebar.jsp" %>
        <div class="form-card">
            <h2>Create New Bank</h2>

            <!-- ✅ CONNECTED TO BankController -->
            <form action="BankController" method="post" onsubmit="return confirm('Register this bank?')">
                <input type="hidden" name="action" value="add">

                <div class="form-group">
                    <label for="bankName">Bank Name</label>
                    <input type="text" id="bankName" name="bankName"
                           placeholder="Enter bank name" required>
                </div>

                <div class="form-group">
                    <label for="bankPhone">Bank Phone Number</label>
                    <input type="text" id="bankPhone" name="bankPhone"
                           placeholder="Enter head office contact number" required>
                </div>

                <div class="form-group">
                    <label for="bankAddress">Bank Address</label>
                    <textarea id="bankAddress" name="bankAddress" rows="4"
                              placeholder="Enter office branch address" required></textarea>
                </div>

                <button type="submit" class="submit-btn">Register Bank</button>
            </form>
        </div>
    </div>

</body>
</html>
