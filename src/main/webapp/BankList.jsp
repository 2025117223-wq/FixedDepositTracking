<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
<c:if test="${listBank == null}"><c:redirect url="BankController?action=list"/></c:if>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Bank Directory</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/BankList.css">
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
    </div>

    <div class="main-content">
        <div class="header"><h1>Bank Directory</h1></div>
        <div class="page-content">
            
            <%-- Popup Message --%>
            <c:if test="${not empty param.msg}">
                <div class="alert-banner" id="toast">
                    <span><strong>Success!</strong> <c:out value="${param.bn}"/> 
                    ${param.msg == 'created' ? 'has been created.' : 'details have been updated.'}</span>
                </div>
                <script>setTimeout(() => { document.getElementById('toast').style.display='none'; }, 5000);</script>
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