<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Bean.Staff" %>

<%
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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Bank - Fixed Deposit Tracking System</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="css/CreateBank.css">
</head>
<body>

<%@ include file="includes/sidebar.jsp" %>

<div class="main-content">

    <div class="header">
        <h1>Create Bank</h1>

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

    <div class="page-content">
        <div class="card">
            <div class="card-header">
                <h2>Create New Bank</h2>
            </div>

            <form action="BankController" method="post" onsubmit="return confirm('Register this bank?')">
                <input type="hidden" name="action" value="add">

                <div class="form-group">
                    <label for="bankName">Bank Name</label>
                    <input type="text" id="bankName" name="bankName" placeholder="Enter bank name" required>
                </div>

                <div class="form-group">
                    <label for="bankPhone">Bank Phone Number</label>
                    <input type="text" id="bankPhone" name="bankPhone" placeholder="Enter head office contact number" required>
                </div>

                <div class="form-group">
                    <label for="bankAddress">Bank Address</label>
                    <textarea id="bankAddress" name="bankAddress" rows="4" placeholder="Enter office branch address" required></textarea>
                </div>

                <button type="submit" class="btn-primary">Register Bank</button>
                <a href="BankController?action=list" class="btn-link">Back to Bank List</a>
            </form>
        </div>
    </div>

</div>

</body>
</html>
