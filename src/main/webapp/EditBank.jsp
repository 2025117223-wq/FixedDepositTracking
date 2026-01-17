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
    <title>Edit Bank - Fixed Deposit Tracking System</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/EditBank.css">
</head>
<body>

    <%@ include file="includes/sidebar.jsp" %>

    <div class="main-content">
        <div class="header">
            <h1>Update Bank</h1>

            <div class="user-profile">
                <div class="user-info">
                    <div class="user-name"><%= staffName %></div>
                    <div class="user-role"><%= staffRole %></div>
                </div>

                <div class="user-avatar">
                    <img src="ProfileImagesServlet" alt="User Avatar"
                         onerror="this.src='images/icons/user.jpg'">
                </div>
            </div>
        </div>

        <div class="form-card">
            <h2>Update Bank Details</h2>

            <form action="BankController" method="post" onsubmit="return confirm('Update bank details?')">
                <input type="hidden" name="action" value="update">

                <div class="form-group">
                    <label>Bank ID</label>
                    <input type="text" name="bankId" value="${bank.bankId}" class="readonly-field" readonly>
                </div>

                <div class="form-group">
                    <label>Bank Name</label>
                    <input type="text" name="bankName" value="${bank.bankName}" class="readonly-field" readonly>
                </div>

                <div class="form-group">
                    <label for="bankPhone">Update Phone Number</label>
                    <input type="text" id="bankPhone" name="bankPhone" value="${bank.bankPhone}" required>
                </div>

                <div class="form-group">
                    <label for="bankAddress">Update Address</label>
                    <textarea id="bankAddress" name="bankAddress" rows="4" required>${bank.bankAddress}</textarea>
                </div>

                <button type="submit" class="submit-btn">Save Changes</button>
                <a href="BankController?action=list" class="cancel-link">Cancel and Go Back</a>
            </form>
        </div>
    </div>

</body>
</html>
