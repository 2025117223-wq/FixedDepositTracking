<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- 
    Safety Check: If this page is accessed directly (listBank is null), 
    redirect to the Controller so data can be fetched from the DAO.
--%>
<c:if test="${listBank == null}">
    <c:redirect url="BankController?action=list"/>
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bank Lists - Fixed Deposit Tracking System</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: #f5f5f5; display: flex; min-height: 100vh; }
        .main-content { margin-left: 250px; flex: 1; display: flex; flex-direction: column; }
        .header { background: white; padding: 20px 40px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); }
        .header h1 { font-size: 2rem; color: #2c3e50; font-weight: 600; }
        .user-profile { display: flex; align-items: center; gap: 15px; }
        .user-info { text-align: right; }
        .user-name { font-weight: 600; color: #2c3e50; }
        .user-role { font-size: 0.85rem; color: #7f8c8d; }
        .user-avatar { width: 50px; height: 50px; border-radius: 50%; background: #003f5c; color: white; display: flex; align-items: center; justify-content: center; font-weight: bold; }
        .page-content { padding: 40px; flex: 1; }
        .table-container { background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); }
        table { width: 100%; border-collapse: collapse; }
        th { padding: 20px; text-align: center; font-weight: 600; color: #2c3e50; border-bottom: 2px solid #e0e0e0; background: #fafafa; }
        td { padding: 20px; text-align: center; color: #2c3e50; border-bottom: 1px solid #f0f0f0; }
		.action-buttons { display: flex; gap: 15px; align-items: center; justify-content: center;
		}
        .btn-edit { 
            background: #e3f2fd; color: #1976d2; padding: 8px 12px; border-radius: 6px; 
            text-decoration: none; font-size: 13px; font-weight: 600; display: flex; align-items: center; gap: 5px;
            transition: 0.2s;
        }
        .btn-edit:hover { background: #1976d2; color: white; }
        .no-data { text-align: center; padding: 40px; color: #95a5a6; font-style: italic; }
    </style>
</head>
<body>

    <%@ include file="includes/sidebar.jsp" %>

    <div class="main-content">
        <div class="header">
            <h1>Bank Directory</h1>
            <div class="user-profile">
                <div class="user-info">
                    <div class="user-name">Nor Azlina</div>
                    <div class="user-role">Administrator</div>
                </div>
                <div class="user-avatar">NA</div>
            </div>
        </div>

        <div class="page-content">
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                        	<th style="width: 15%;">Bank ID</th>
                            <th style="width: 25%;">Bank Name</th>
                            <th style="width: 35%;">Address</th>
                            <th style="width: 15%;">Phone</th>
                            <th style="width: 15%;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty listBank}">
                                <c:forEach var="bank" items="${listBank}">
                                    <tr>
                                    	<td>${bank.bankId}</td>
                                        <td>${bank.bankName}</td>
                                        <td>${bank.bankAddress}</td>
                                        <td>${bank.bankPhone}</td>
                                        <td>
                                            <div class="action-buttons">
                                                <a href="BankController?action=edit&id=${bank.bankId}" class="btn-edit">
                                                    <span>✏️</span> Edit
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="no-data">No banks registered in the system.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
