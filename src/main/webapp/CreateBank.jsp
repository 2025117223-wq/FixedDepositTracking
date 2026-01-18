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
    <title>Create Bank - Fixed Deposit Tracking System</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', sans-serif; background: #f5f5f5; display: flex; }

        .main-content { margin-left: 250px; flex: 1; min-height: 100vh; }

        /* ✅ add header styling (same vibe as BankList) */
        .header {
            background: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.08);
        }

        .page-content {
            padding: 40px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: calc(100vh - 80px);
        }

        .form-card { background: white; padding: 40px; border-radius: 15px; box-shadow: 0 10px 25px rgba(0,0,0,0.05); width: 100%; max-width: 500px; }
        .form-card h2 { color: #2c3e50; margin-bottom: 30px; font-size: 1.5rem; text-align: center; }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; font-weight: 600; color: #34495e; margin-bottom: 8px; }
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-family: inherit;
            font-size: 14px;
        }
        .form-group input:focus, .form-group textarea:focus { border-color: #003f5c; outline: none; }
        .submit-btn {
            background: #003f5c;
            color: white;
            border: none;
            padding: 15px;
            width: 100%;
            border-radius: 8px;
            font-weight: 700;
            cursor: pointer;
            font-size: 16px;
            margin-top: 10px;
        }
        .submit-btn:hover { background: #002d42; }
    </style>
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
        <div class="form-card">
            <h2>Create New Bank</h2>

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

                <button type="submit" class="submit-btn">Register Bank</button>
            </form>
        </div>
    </div>

</div>

</body>
</html>
