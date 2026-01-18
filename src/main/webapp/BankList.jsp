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

<c:if test="${listBank == null}">
    <c:redirect url="BankController?action=list"/>
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Bank Directory</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: #f5f5f5; display: flex; }
        .main-content { margin-left: 250px; flex: 1; }
        .header { background: white; padding: 20px 40px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .page-content { padding: 40px; }

        .alert-banner {
            padding: 15px 25px; background: #27ae60; color: white; border-radius: 8px;
            margin-bottom: 25px; display: flex; align-items: center;
            animation: slideDown 0.4s ease; box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        @keyframes slideDown { from { transform: translateY(-10px); opacity: 0; } to { transform: translateY(0); opacity: 1; } }

        .table-container { background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 18px; text-align: left; border-bottom: 1px solid #eee; }
        th { background: #fafafa; color: #2c3e50; }
        .btn-edit { color: #1976d2; text-decoration: none; font-weight: 600; }
    </style>
</head>
<body>

<%@ include file="includes/sidebar.jsp" %>

<div class="main-content">

    <div class="header">
        <h1>Bank Directory</h1>

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

        <%-- Popup Message --%>
        <c:if test="${not empty param.msg}">
            <div class="alert-banner" id="toast">
                <span>
                    <strong>Success!</strong>
                    <c:out value="${param.bn}"/>
                    <c:choose>
                        <c:when test="${param.msg eq 'created'}"> has been created.</c:when>
                        <c:otherwise> details have been updated.</c:otherwise>
                    </c:choose>
                </span>
            </div>
            <script>
                setTimeout(function () {
                    var t = document.getElementById('toast');
                    if (t) t.style.display = 'none';
                }, 5000);
            </script>
        </c:if>

        <div class="table-container">
            <table>
                <thead>
                <tr><th>ID</th><th>Bank Name</th><th>Address</th><th>Phone</th><th>Actions</th></tr>
                </thead>
                <tbody>
                <c:forEach var="bank" items="${listBank}">
                    <tr>
                        <td>${bank.bankId}</td>
                        <td>${bank.bankName}</td>
                        <td>${bank.bankAddress}</td>
                        <td>${bank.bankPhone}</td>
                        <td><a href="BankController?action=edit&id=${bank.bankId}" class="btn-edit">✏️ Edit</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

    </div>
</div>

</body>
</html>
