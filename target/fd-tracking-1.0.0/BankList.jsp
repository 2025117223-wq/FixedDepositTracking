<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fd.dao.BankDAO" %>
<%@ page import="com.fd.model.Bank" %>
<%@ page import="java.util.List" %>
<%
    // Session check
    String userName = (String) session.getAttribute("staffName");
    String userRole = (String) session.getAttribute("staffRole");
    
    if (userName == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
    
    // Load banks from database
    BankDAO bankDAO = new BankDAO();
    List<Bank> bankList = null;
    try {
        bankList = bankDAO.getAllBanks();
        System.out.println("========================================");
        System.out.println("🏦 BankList.jsp: Loaded " + bankList.size() + " banks from database");
        System.out.println("========================================");
    } catch (Exception e) {
        System.err.println("❌ Error loading banks: " + e.getMessage());
        e.printStackTrace();
    }
    
    // Build JSON for JavaScript
    StringBuilder jsonBuilder = new StringBuilder();
    jsonBuilder.append("[");
    if (bankList != null) {
        for (int i = 0; i < bankList.size(); i++) {
            Bank b = bankList.get(i);
            jsonBuilder.append("{");
            jsonBuilder.append("\"id\":").append(b.getBankId()).append(",");
            jsonBuilder.append("\"name\":\"").append(b.getBankName() != null ? b.getBankName().replace("\"", "\\\"") : "").append("\",");
            jsonBuilder.append("\"phone\":\"").append(b.getBankPhone() != null ? b.getBankPhone().replace("\"", "\\\"") : "").append("\",");
            jsonBuilder.append("\"address\":\"").append(b.getBankAddress() != null ? b.getBankAddress().replace("\"", "\\\"") : "").append("\"");
            jsonBuilder.append("}");
            if (i < bankList.size() - 1) jsonBuilder.append(",");
        }
    }
    jsonBuilder.append("]");
    String banksJson = jsonBuilder.toString();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bank Lists - Fixed Deposit Tracking System</title>
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

        .main-content {
            margin-left: 250px;
            flex: 1;
            display: flex;
            flex-direction: column;
        }

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
            cursor: pointer;
        }

        .user-avatar img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            object-fit: cover;
        }

        .page-content {
            padding: 40px;
            flex: 1;
        }

        .table-container {
            background: white;
            border-radius: 0;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: white;
        }

        th {
            padding: 25px 20px;
            text-align: center;
            font-weight: 600;
            color: #2c3e50;
            font-size: 16px;
            border-bottom: 2px solid #e0e0e0;
        }

        td {
            padding: 25px 20px;
            text-align: center;
            color: #2c3e50;
            font-size: 15px;
            border-bottom: 1px solid #f0f0f0;
        }

        tbody tr:hover {
            background: #f8f9fa;
        }

        .action-buttons {
            display: flex;
            gap: 15px;
            align-items: center;
            justify-content: center;
        }

        .action-btn {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 5px;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .action-btn:hover {
            opacity: 0.7;
        }

        .action-icon {
            width: 35px;
            height: 35px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
        }

        .action-icon.update {
            color: #3498db;
        }

        .action-label {
            font-size: 12px;
            color: #7f8c8d;
            font-weight: 500;
        }

        .no-data {
            text-align: center;
            padding: 60px 20px;
            color: #7f8c8d;
            font-size: 16px;
        }

        .success-message {
            position: fixed;
            top: 100px;
            left: 50%;
            transform: translateX(-50%);
            background: #80cbc4;
            color: white;
            padding: 15px 40px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 500;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            z-index: 10000;
            opacity: 0;
            display: block;
        }
        
        .success-message.show {
            animation: slideDown 0.4s ease forwards;
        }
        
        .success-message.hide {
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
    </style>
</head>
<body>
    <%@ include file="includes/sidebar.jsp" %>

    <div class="main-content">
        <div class="header">
            <h1>Bank Lists</h1>
            <div class="user-profile">
                <div class="user-info">
                    <div class="user-name"><%= userName %></div>
                    <div class="user-role"><%= userRole %></div>
                </div>
                <div class="user-avatar" onclick="window.location.href='Profile.jsp'">
                    <img src="images/icons/user.jpg" alt="User Avatar" onerror="this.style.display='none'">
                </div>
            </div>
        </div>

        <div class="page-content">
            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Bank ID</th>
                            <th>Name</th>
                            <th>Phone Number</th>
                            <th>Address</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="bankTableBody"></tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Success Message -->
    <div class="success-message" id="successMessage"></div>

    <script>
        // Load banks from database
        const banks = <%= banksJson %>;
        
        console.log("========================================");
        console.log("🏦 Loaded " + banks.length + " banks from database");
        if (banks.length > 0) {
            console.log("✅ First bank:", banks[0]);
        }
        console.log("========================================");

        document.addEventListener('DOMContentLoaded', function() {
            const bankDropdown = document.getElementById('bankDropdown');
            const bankNavItem = document.getElementById('bankNavItem');
            
            if (bankDropdown && bankNavItem) {
                bankDropdown.classList.add('show');
                bankNavItem.classList.add('open');
            }

            loadBanks();

            // Check for success messages
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('msg') === 'created') {
                showSuccessMessage('New bank added successfully!');
                window.history.replaceState({}, document.title, 'BankList.jsp');
            } else if (urlParams.get('msg') === 'updated') {
                showSuccessMessage('Bank updated successfully!');
                window.history.replaceState({}, document.title, 'BankList.jsp');
            }
        });

        function loadBanks() {
            const tbody = document.getElementById('bankTableBody');
            tbody.innerHTML = '';

            if (banks.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="no-data">No banks found. Add a new bank to get started.</td></tr>';
                return;
            }

            banks.forEach(bank => {
                const row = document.createElement('tr');
                row.innerHTML = 
                    '<td>' + bank.id + '</td>' +
                    '<td>' + bank.name + '</td>' +
                    '<td>' + bank.phone + '</td>' +
                    '<td>' + bank.address + '</td>' +
                    '<td>' +
                        '<div class="action-buttons">' +
                            '<div class="action-btn" onclick="updateBank(' + bank.id + ')">' +
                                '<div class="action-icon update">✏️</div>' +
                                '<div class="action-label">Update</div>' +
                            '</div>' +
                        '</div>' +
                    '</td>';
                tbody.appendChild(row);
            });
            
            console.log("✅ Table populated with " + banks.length + " banks");
        }

        function updateBank(bankId) {
            // Redirect to EditBank.jsp with bank ID
            window.location.href = '${pageContext.request.contextPath}/BankServlet?action=edit&id=' + bankId;
        }

        function showSuccessMessage(message) {
            const successMsg = document.getElementById('successMessage');
            successMsg.textContent = message;
            successMsg.classList.add('show');
            successMsg.classList.remove('hide');

            setTimeout(function() {
                successMsg.classList.remove('show');
                successMsg.classList.add('hide');
                
                setTimeout(function() {
                    successMsg.classList.remove('hide');
                }, 400);
            }, 3000);
        }
    </script>
</body>
</html>
