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
    <c:redirect url="${pageContext.request.contextPath}/BankController?action=list"/>
</c:if>

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
                <img src="${pageContext.request.contextPath}/ProfileImagesServlet" alt="User Avatar"
                     onerror="this.src='${pageContext.request.contextPath}/images/icons/user.jpg'">
            </div>
        </div>
    </div>

    <div class="page-content">

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
                setTimeout(() => {
                    const t = document.getElementById('toast');
                    if (t) t.style.display = 'none';
                    window.history.replaceState({}, document.title, window.location.pathname);
                }, 5000);
            </script>
        </c:if>

        <div class="table-container">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Bank Name</th>
                    <th>Address</th>
                    <th>Phone</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="bank" items="${listBank}">
                    <tr>
                        <td><c:out value="${bank.bankId}"/></td>
                        <td><c:out value="${bank.bankName}"/></td>
                        <td><c:out value="${bank.bankAddress}"/></td>
                        <td><c:out value="${bank.bankPhone}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/BankController?action=edit&id=${bank.bankId}"
                               class="btn-edit">✏️ Edit</a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty listBank}">
                    <tr>
                        <td colspan="5" style="text-align:center; padding:16px;">
                            No banks found.
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

    </div>
</div>

</body>
</html>
