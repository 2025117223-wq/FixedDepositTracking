<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Bean.Staff" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    Staff loggedStaff = (Staff) session.getAttribute("loggedStaff");
    if (loggedStaff == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    String staffName = loggedStaff.getStaffName();
    String staffRole = loggedStaff.getStaffRole();
%>

<c:if test="${bank == null}">
    <c:redirect url="BankController?action=list"/>
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Bank - Fixed Deposit Tracking System</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="css/EditBank.css">
</head>
<body>

<%@ include file="includes/sidebar.jsp" %>

<div class="main-content">

    <div class="header">
        <h1>Edit Bank</h1>

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
                <h2>Update Bank Details</h2>
            </div>

            <form action="BankController" method="post" onsubmit="return confirm('Update bank details?')">
                <input type="hidden" name="action" value="update">

                <div class="form-group">
                    <label>Bank ID</label>
                    <input type="text" name="bankId" value="<c:out value='${bank.bankId}'/>" readonly class="readonly">
                </div>

                <div class="form-group">
                    <label>Bank Name</label>
                    <input type="text" name="bankName" value="<c:out value='${bank.bankName}'/>" readonly class="readonly">
                </div>

                <div class="form-group">
                    <label for="bankPhone">Update Phone Number</label>
                    <input type="text" id="bankPhone" name="bankPhone" value="<c:out value='${bank.bankPhone}'/>" required>
                </div>

                <div class="form-group">
                    <label for="bankAddress">Update Address</label>
                    <textarea id="bankAddress" name="bankAddress" rows="4" required><c:out value="${bank.bankAddress}"/></textarea>
                </div>

                <button type="submit" class="btn-primary">Save Changes</button>
                <a href="BankController?action=list" class="btn-link">Cancel and Go Back</a>
            </form>
        </div>
    </div>

</div>

</body>
</html>
