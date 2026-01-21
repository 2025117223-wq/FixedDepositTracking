<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Simple session check - No includes needed!
    String userName = (String) session.getAttribute("staffName");
    String userRole = (String) session.getAttribute("staffRole");
    
    if (userName == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<%@ page import="java.util.List" %>
<%@ page import="com.fd.model.FixedDepositRecord" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fixed Deposits Lists - Fixed Deposit Tracking System</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: #f5f5f5;
            display: flex;
            min-height: 100vh;
        }

        /* Main Content */
        .main-content {
            margin-left: 250px;
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        /* Header */
        .header {
            background: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .header h1 {
            font-size: 2rem;
            color: #2c3e50;
            font-weight: 600;
        }

        .user-profile {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .user-info {
            text-align: right;
        }

        .user-name {
            font-weight: 600;
            color: #2c3e50;
            font-size: 16px;
        }

        .user-role {
            font-size: 13px;
            color: #7f8c8d;
        }

        .user-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #d0d0d0;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            position: relative;
        }

        .user-avatar img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            object-fit: cover;
        }

        /* Page Content */
        .page-content {
            padding: 40px;
            flex: 1;
        }

        /* Error Message */
        .alert {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        /* Search Bar */
        .search-section {
            margin-bottom: 30px;
        }

        .search-bar {
            width: 100%;
            max-width: 400px;
            position: relative;
        }

        .search-bar input {
            width: 100%;
            padding: 15px 50px 15px 20px;
            border: 2px solid #d0d0d0;
            border-radius: 50px;
            font-size: 15px;
            font-family: 'Inter', sans-serif;
            transition: all 0.3s ease;
        }

        .search-bar input:focus {
            outline: none;
            border-color: #1a4d5e;
        }

        .search-icon {
            position: absolute;
            right: 20px;
            top: 50%;
            transform: translateY(-50%);
            color: #7f8c8d;
            font-size: 20px;
        }

        /* Table */
        .table-container {
            background: white;
            border-radius: 20px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: #f8f9fa;
        }

        th {
            padding: 20px;
            text-align: center;
            font-weight: 600;
            color: #2c3e50;
            font-size: 15px;
            border-bottom: 2px solid #e0e0e0;
        }

        td {
            padding: 20px;
            text-align: center;
            color: #2c3e50;
            font-size: 14px;
            border-bottom: 1px solid #f0f0f0;
        }

        tbody tr:hover {
            background: #f8f9fa;
        }

        .account-link {
            color: #3498db;
            text-decoration: underline;
            cursor: pointer;
        }

        .account-link:hover {
            color: #2980b9;
        }

        .status {
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 500;
            display: inline-block;
        }

        .status.pending {
            background: #ffe5e5;
            color: #c0392b;
        }

        .status.ongoing {
            background: #fff9e5;
            color: #d68910;
        }

        .status.matured {
            background: #e5f5f0;
            color: #0c7a5a;
        }

        .action-btn {
            display: inline-flex;
            flex-direction: column;
            align-items: center;
            gap: 5px;
            cursor: pointer;
            margin-top: 10px;
            transition: all 0.3s ease;
        }

        .action-btn:hover {
            opacity: 0.7;
        }

        .action-icon {
            width: 30px;
            height: 30px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
        }

        .action-icon.update {
            color: #3498db;
        }

        .action-label {
            font-size: 11px;
            color: #7f8c8d;
            font-weight: 500;
        }
        
        .action-icon img {
		    width: 100%;
		    height: 100%;
		    object-fit: contain;
		    display: block; /* removes default inline gap */
		}

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #7f8c8d;
        }

        .empty-state-icon {
            font-size: 60px;
            margin-bottom: 20px;
        }

        .empty-state-text {
            font-size: 18px;
            margin-bottom: 10px;
        }

        .empty-state-subtext {
            font-size: 14px;
        }

        @media (max-width: 768px) {
            .sidebar {
                width: 200px;
            }

            .main-content {
                margin-left: 200px;
            }

            .header {
                padding: 15px 20px;
            }

            .header h1 {
                font-size: 1.5rem;
            }

            .page-content {
                padding: 20px;
            }

            .table-container {
                overflow-x: auto;
            }
        }
        
        /* Notification Message */
        .notification {
            position: fixed;
            top: 100px;
            left: 50%;
            transform: translateX(-50%);
            padding: 15px 30px;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            font-size: 14px;
            font-weight: 500;
            z-index: 10000;
            color: white;
            min-width: 300px;
            text-align: center;
            margin-left: 100px;
            margin-top: -50px;
            opacity: 0;
            display: block;
        }

        .notification.show {
            animation: slideDown 0.4s ease forwards;
        }

        .notification.hide {
            animation: slideUpFade 0.4s ease forwards;
        }

        @keyframes slideDown {
            from {
                transform: translateX(-50%) translateY(-20px);
                opacity: 0;
            }
            to {
                transform: translateX(-50%) translateY(0);
                opacity: 1;
            }
        }

        @keyframes slideUpFade {
            from {
                transform: translateX(-50%) translateY(0);
                opacity: 1;
            }
            to {
                transform: translateX(-50%) translateY(-20px);
                opacity: 0;
            }
        }

        .notification.success {
            background: #80cbc4;
        }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <%@ include file="includes/sidebar.jsp" %>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Header -->
        <div class="header">
            <h1>Fixed Deposits Lists</h1>
            <div class="user-profile">
                <div class="user-info">
                    <div class="user-name"><%= userName %></div>
                    <div class="user-role"><%= userRole %></div>
                </div>
                <div class="user-avatar" onclick="window.location.href='Profile.jsp'" style="cursor: pointer;">
                    <img src="images/icons/user.jpg" alt="User Avatar" onerror="this.style.display='none'">
                </div>
            </div>
        </div>

        <!-- Page Content -->
        <div class="page-content">
            <!-- Error Message -->
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <!-- Search Bar -->
            <div class="search-section">
                <div class="search-bar">
                    <input type="text" id="searchInput" placeholder="Search" onkeyup="searchTable()">
                    <span class="search-icon">🔍</span>
                </div>
            </div>

            <!-- Table -->
            <div class="table-container">
                <table id="fdTable">
                    <thead>
                        <tr>
                            <th>Fixed Deposit ID</th>
                            <th>Account No.</th>
                            <th>Deposits (RM)</th>
                            <th>Bank</th>
                            <th>Tenure (Months)</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<FixedDepositRecord> fdList = (List<FixedDepositRecord>) request.getAttribute("fdList");
                            
                            if (fdList != null && !fdList.isEmpty()) {
                                for (FixedDepositRecord fd : fdList) {
                        %>
                        <tr>
                            <td>FD<%= fd.getFdID() %></td>
                            <td>
                                <a href="ViewFDServlet?id=<%= fd.getFdID() %>" class="account-link">
                                    <%= fd.getAccNumber() %>
                                </a>
                            </td>
                            <td><%= fd.getFormattedDepositAmount() %></td>
                            <td><%= fd.getBankName() != null ? fd.getBankName() : "-" %></td>
                            <td><%= fd.getTenure() %></td>
                            <td>
                                <span class="status <%= fd.getStatusClass() %>">
                                    <%= fd.getDisplayStatus() %>
                                </span>
                            </td>
                            <td>
                                <div class="action-btn" onclick="updateFD(<%= fd.getFdID() %>)">
								    <div class="action-icon update">
								        <img src="images/icons/update-icon.png" alt="Update" style="width:100px; height:90px;">
								    </div>
								    	<div class="action-label">Update</div>
								</div>
                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="7">
                                <div class="empty-state">
                                    <div class="empty-state-icon">📋</div>
                                    <div class="empty-state-text">No Fixed Deposits Found</div>
                                    <div class="empty-state-subtext">Create a new Fixed Deposit to get started</div>
                                </div>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        // Show notification function
        function showNotification(message, type) {
            const existingNotification = document.querySelector('.notification');
            if (existingNotification) {
                existingNotification.remove();
            }

            const notification = document.createElement('div');
            notification.className = 'notification';
            notification.textContent = message;
            
            if (type === 'success') {
                notification.classList.add('success');
            }
            
            document.body.appendChild(notification);

            setTimeout(() => {
                notification.classList.add('show');
            }, 10);

            setTimeout(() => {
                notification.classList.remove('show');
                notification.classList.add('hide');
                setTimeout(() => notification.remove(), 400);
            }, 3000);
        }

        // Search function
        function searchTable() {
            const input = document.getElementById('searchInput');
            const filter = input.value.toUpperCase();
            const table = document.getElementById('fdTable');
            const tr = table.getElementsByTagName('tr');

            for (let i = 1; i < tr.length; i++) {
                let found = false;
                const td = tr[i].getElementsByTagName('td');
                
                for (let j = 0; j < td.length; j++) {
                    if (td[j]) {
                        const txtValue = td[j].textContent || td[j].innerText;
                        if (txtValue.toUpperCase().indexOf(filter) > -1) {
                            found = true;
                            break;
                        }
                    }
                }
                
                tr[i].style.display = found ? '' : 'none';
            }
        }

        // Update FD function - go through servlet to load data from database
        function updateFD(fdId) {
            window.location.href = 'UpdateFDServlet?id=' + fdId;
        }

        // Auto-open Fixed Deposits dropdown
        document.addEventListener('DOMContentLoaded', function() {
            const fdDropdown = document.getElementById('fdDropdown');
            const fdNavItem = document.getElementById('fdNavItem');
            
            if (fdDropdown && fdNavItem) {
                fdDropdown.classList.add('show');
                fdNavItem.classList.add('open');
            }
            
            // **FIX: Check for success messages from request attributes (set by servlet)**
            <% 
            String fdSuccess = (String) request.getAttribute("fdSuccess");
            Integer newFdID = (Integer) request.getAttribute("newFdID");
            String fdUpdateSuccess = (String) request.getAttribute("fdUpdateSuccess");
            String updatedFdId = (String) request.getAttribute("updatedFdId");
            
            if ("true".equals(fdSuccess)) { 
            %>
                showNotification('New Fixed Deposit <%= newFdID != null ? "FD" + newFdID : "" %> added successfully!', 'success');
            <% } %>
            
            <% if ("true".equals(fdUpdateSuccess)) { %>
                showNotification('Fixed Deposit <%= updatedFdId != null ? updatedFdId : "" %> has been updated!', 'success');
            <% } %>
        });
    </script>
</body>
</html>
