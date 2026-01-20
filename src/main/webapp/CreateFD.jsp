<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fd.model.Bank" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%
    // Get data from session (if user clicked Back from ApplicationForm)
    String accountNumber = session.getAttribute("accountNumber") != null ? (String) session.getAttribute("accountNumber") : "";
    String referralNumber = session.getAttribute("referralNumber") != null ? (String) session.getAttribute("referralNumber") : "";
    String bankName = session.getAttribute("bankName") != null ? (String) session.getAttribute("bankName") : "";
    String depositAmount = session.getAttribute("depositAmount") != null ? session.getAttribute("depositAmount").toString() : "";
    String interestRate = session.getAttribute("interestRate") != null ? session.getAttribute("interestRate").toString() : "";
    String startDate = session.getAttribute("startDate") != null ? session.getAttribute("startDate").toString() : "";
    String tenure = session.getAttribute("tenure") != null ? session.getAttribute("tenure").toString() : "";
    String maturityDate = session.getAttribute("maturityDate") != null ? session.getAttribute("maturityDate").toString() : "";
    String certNo = session.getAttribute("certNo") != null ? (String) session.getAttribute("certNo") : "";
    String fdType = session.getAttribute("fdType") != null ? (String) session.getAttribute("fdType") : "";
    
    // Free FD fields
    String autoRenewalStatus = session.getAttribute("autoRenewalStatus") != null ? (String) session.getAttribute("autoRenewalStatus") : "";
    String withdrawableStatus = session.getAttribute("withdrawableStatus") != null ? (String) session.getAttribute("withdrawableStatus") : "";
    
    // Pledge FD fields
    String pledgeValue = session.getAttribute("pledgeValue") != null ? session.getAttribute("pledgeValue").toString() : "";
    String collateralStatus = session.getAttribute("collateralStatus") != null ? (String) session.getAttribute("collateralStatus") : "";
    
    // **FIX: Get reminder settings from session**
    String reminderMaturity = session.getAttribute("reminderMaturity") != null ? (String) session.getAttribute("reminderMaturity") : "";
    String reminderIncomplete = session.getAttribute("reminderIncomplete") != null ? (String) session.getAttribute("reminderIncomplete") : "";
%>
<%
    
    // Get bank list from request (set by CreateFDServlet)
    @SuppressWarnings("unchecked")
    java.util.List<com.fd.model.Bank> bankList = (java.util.List<Bank>) request.getAttribute("bankList");
    if (bankList == null) {
        bankList = new java.util.ArrayList<Bank>();
    }
%>
<%
    // ========== DEBUG - CHECK BANK LIST ==========
    System.out.println("===== DEBUG CreateFD.jsp =====");
    System.out.println("bankList: " + bankList);
    System.out.println("bankList size: " + (bankList != null ? bankList.size() : "null"));
    System.out.println("bankList class: " + (bankList != null ? bankList.getClass().getName() : "null"));
    if (bankList != null && !bankList.isEmpty()) {
        System.out.println("First bank: " + bankList.get(0).getBankName());
        System.out.println("First bank class: " + bankList.get(0).getClass().getName());
    } else {
        System.out.println("WARNING: Bank list is empty or null!");
    }
    System.out.println("==============================");
%>
<%
    // Simple session check - No includes needed!
    String userName = (String) session.getAttribute("staffName");
    String userRole = (String) session.getAttribute("staffRole");
    
    if (userName == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Fixed Deposits - Fixed Deposit Tracking System</title>
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
            margin-top: 35px;
            flex: 1;
        }

        /* Error/Success Messages */
        .alert {
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-size: 14px;
            font-weight: 500;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        /* Form Container */
        .form-container {
            background: white;
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            border-top: 5px solid #ff8c42;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 30px;
            margin-bottom: 30px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group label {
            font-size: 14px;
            font-weight: 500;
            color: #2c3e50;
            margin-bottom: 8px;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            padding: 12px 15px;
            border: 2px solid #d0d0d0;
            border-radius: 8px;
            font-size: 14px;
            font-family: 'Inter', sans-serif;
            transition: all 0.3s ease;
            background-color: white;
        }

        .form-group select {
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%237f8c8d' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 15px center;
            padding-right: 40px;
            cursor: pointer;
        }

        .form-group select:hover {
            border-color: #1a4d5e;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #1a4d5e;
        }

        .form-group input::placeholder {
            color: #adb5bd;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }

        .form-group.full-width {
            grid-column: span 3;
        }

        .form-group.half-width {
            grid-column: span 1;
        }

        /* File Upload */
        .file-upload-wrapper {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .file-upload-btn {
            padding: 4px 32px 4px 8px;
            background: white;
            border: 2px solid #d0d0d0;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            color: #2c3e50;
            cursor: pointer;
            transition: all 0.3s ease;
            font-family: 'Inter', sans-serif;
            display: flex;
            align-items: center;
            justify-content: flex-start;
            gap: 10px;
            width: 100%;
            box-sizing: border-box;
        }

        .file-upload-btn:hover {
            background: #e9ecef;
            border-color: #1a4d5e;
        }

        .file-upload-btn input[type="file"] {
            display: none;
        }

        .file-icon {
            font-size: 24px;
        }

        .file-text {
            font-size: 14px;
            color: #2c3e50;
        }

        .file-info {
            font-size: 12px;
            color: #7f8c8d;
            margin-top: 5px;
        }

        /* Calculate Link */
        .calculate-link {
            color: #1a4d5e;
            font-size: 13px;
            text-decoration: none;
            margin-top: 5px;
            display: inline-block;
        }

        .calculate-link:hover {
            text-decoration: underline;
        }

        /* Reminder Section */
        .reminder-section {
            margin-top: 30px;
            padding-top: 30px;
            border-top: 2px solid #f0f0f0;
        }

        .reminder-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 20px;
            max-width: 500px;
        }

        .reminder-label {
            font-size: 14px;
            color: #2c3e50;
        }

        /* Toggle Switch */
        .toggle-switch {
            position: relative;
            width: 50px;
            height: 26px;
        }

        .toggle-switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #ccc;
            transition: .4s;
            border-radius: 26px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #1a4d5e;
        }

        input:checked + .slider:before {
            transform: translateX(24px);
        }

        /* Form Actions */
        .form-actions {
            display: flex;
            gap: 15px;
            justify-content: flex-end;
            margin-top: 30px;
        }

        .btn {
            padding: 10px 35px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            font-family: 'Inter', sans-serif;
        }

        .back-button {
            position: absolute;
            top: 110px;
            left: 290px;
            display: flex;
            align-items: center;
            gap: 5px;
            color: #7f8c8d;
            font-size: 16px;
            cursor: pointer;
            text-decoration: none;
            width: fit-content;
        }

        .back-button:hover {
            color: #2c3e50;
        }

        .back-icon {
            font-size: 24px;
        }

        .btn-primary {
            background: #1a4d5e;
            color: white;
        }

        .btn-primary:hover {
            background: #153d4a;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(26, 77, 94, 0.3);
        }

        /* Notification */
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
            margin-left: 125px;
            opacity: 0;
            transition: opacity 0.3s ease, transform 0.3s ease;
        }

        .notification.show {
            opacity: 1;
            transform: translateX(-50%) translateY(0);
        }

        .notification.hide {
            opacity: 0;
            transform: translateX(-50%) translateY(-20px);
        }

        .notification.turned-on {
            background: #80cbc4;
        }

        .notification.turned-off {
            background: #e57373;
        }

        /* Modal Popup */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            z-index: 2000;
            justify-content: center;
            align-items: center;
        }

        .modal-overlay.show {
            display: flex;
        }

        .modal-content {
            background: white;
            border-radius: 20px;
            padding: 40px;
            width: 90%;
            max-width: 500px;
            position: relative;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
            animation: modalSlideIn 0.3s ease-out;
        }

        @keyframes modalSlideIn {
            from {
                transform: translateY(-50px);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }

        .modal-close {
            position: absolute;
            top: 20px;
            right: 20px;
            font-size: 28px;
            color: #7f8c8d;
            cursor: pointer;
            background: none;
            border: none;
            width: 35px;
            height: 35px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            transition: all 0.3s ease;
        }

        .modal-close:hover {
            background: #f0f0f0;
            color: #2c3e50;
        }

        .modal-title {
            font-size: 2rem;
            color: #2c3e50;
            font-weight: 600;
            text-align: center;
            margin-bottom: 30px;
        }

        .calculation-result {
            margin-bottom: 20px;
        }

        .calculation-result label {
            display: block;
            font-size: 14px;
            color: #2c3e50;
            margin-bottom: 8px;
            font-weight: 500;
        }

        .calculation-result input {
            width: 100%;
            padding: 15px;
            border: 2px solid #d0d0d0;
            border-radius: 8px;
            font-size: 16px;
            font-family: 'Inter', sans-serif;
            background: #f8f9fa;
            color: #2c3e50;
            font-weight: 600;
            text-align: center;
        }

        @media (max-width: 1200px) {
            .form-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .form-group.full-width {
                grid-column: span 2;
            }
        }

        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }

            .form-group.full-width,
            .form-group.half-width {
                grid-column: span 1;
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

            .form-container {
                padding: 20px;
            }
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
            <h1>Create Fixed Deposits</h1>
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
        
            <a href="FDListServlet" class="back-button">
                <span class="back-icon">←</span>
                <span>Back</span>
            </a>
            
            <!-- Error Message Display -->
            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <!-- Success Message Display -->
            <% if (session.getAttribute("successMessage") != null) { %>
                <div class="alert alert-success">
                    <%= session.getAttribute("successMessage") %>
                </div>
                <% session.removeAttribute("successMessage"); %>
            <% } %>

            <div class="form-container">
                <!-- Form submits to CreateFDServlet -->
                <form id="createFDForm" action="CreateFDServlet" method="POST" enctype="multipart/form-data">
                    <div class="form-grid">
                        <!-- Account Number -->
                        <div class="form-group">
                            <label for="accountNumber">Account Number</label>
                            <input type="text" id="accountNumber" name="accountNumber" value="<%= accountNumber %>" required>
                        </div>
                        
                        <!-- Referral Number -->
                        <div class="form-group">
                            <label for="referralNumber">Referral Number</label>
                            <input type="text" id="referralNumber" name="referralNumber" value="<%= referralNumber %>">
                        </div>
                        
                        <!-- Bank Name -->
                        <div class="form-group">
                            <label for="bankName">Bank Name</label>
                            <select id="bankName" name="bankName" required>
                                <option value="">Select Bank</option>
                                <% 
                                if (bankList != null && !bankList.isEmpty()) {
                                    for (com.fd.model.Bank bank : bankList) {
                                        String selected = bank.getBankName().equals(bankName) ? "selected" : "";
                                %>
                                <option value="<%= bank.getBankName() %>" <%= selected %>><%= bank.getBankName() %></option>
                                <% 
                                    }
                                } else {
                                %>
                                <option value="" disabled>No banks available</option>
                                <% } %>
                            </select>
                        </div>
                        
                        <!-- Deposit Amount -->
                        <div class="form-group">
                            <label for="depositAmount">Deposit Amount (RM)</label>
                            <input type="number" id="depositAmount" name="depositAmount" step="0.01" min="0.01" value="<%= depositAmount %>" required>
                        </div>
                        
                        <!-- Interest Rate -->
                        <div class="form-group">
                            <label for="interestRate">Interest Rate (%)</label>
                            <input type="number" id="interestRate" name="interestRate" step="0.001" min="0.001" value="<%= interestRate %>" required>
                            <a href="#" class="calculate-link" onclick="calculateInterest(); return false;">Calculate</a>
                        </div>

                        <!-- Start Date -->
                        <div class="form-group">
                            <label for="startDate">Start Date</label>
                            <input type="date" id="startDate" name="startDate" value="<%= startDate %>" required onchange="calculateMaturityDate()">
                        </div>
                        
                        <!-- Tenure -->
                        <div class="form-group">
                            <label for="tenure">Tenure (Month)</label>
                            <input type="number" id="tenure" name="tenure" min="1" value="<%= tenure %>" required onchange="calculateMaturityDate()">
                        </div>

                        <!-- Maturity Date -->
                        <div class="form-group">
                            <label for="maturityDate">Maturity Date</label>
                            <input type="date" id="maturityDate" name="maturityDate" value="<%= maturityDate %>" required>
                        </div>

                        <!-- FD Certificate Upload -->
                        <div class="form-group">
                            <label for="fdCertificate">FD Certificate</label>
                            <div class="file-upload-wrapper">
                                <label for="fdCertificate" class="file-upload-btn">
                                    <span class="file-icon">📁</span>
                                    <span class="file-text" id="fileText">Choose File</span>
                                    <input type="file" id="fdCertificate" name="fdCertificate" accept=".jpg,.jpeg,.png">
                                </label>
                            </div>
                            <div class="file-info">JPEG, PNG</div>
                        </div>

                        <!-- FD Certificate No. -->
                        <div class="form-group">
                            <label for="fdCertificateNo">FD Certificate No.</label>
                            <input type="text" id="fdCertificateNo" name="fdCertificateNo" value="<%= certNo %>">
                        </div>

                        <!-- FD Type -->
                        <div class="form-group">
                            <label for="fdType">FD Type</label>
                            <select id="fdType" name="fdType" required onchange="handleFDTypeChange()">
                                <option value="">Select FD Type</option>
                                <option value="Free" <%= "Free".equals(fdType) ? "selected" : "" %>>Free</option>
                                <option value="Pledge" <%= "Pledge".equals(fdType) ? "selected" : "" %>>Pledge</option>
                            </select>
                        </div>

                        <!-- Free FD: Auto Renewal Status (Hidden by default) -->
                        <div class="form-group" id="autoRenewalField" style="display: none;">
                            <label for="autoRenewalStatus">Auto Renewal Status</label>
                            <select id="autoRenewalStatus" name="autoRenewalStatus">
                                <option value="">Select Status</option>
                                <option value="Y" <%= "Y".equals(autoRenewalStatus) ? "selected" : "" %>>Yes</option>
                                <option value="N" <%= "N".equals(autoRenewalStatus) ? "selected" : "" %>>No</option>
                            </select>
                        </div>

                        <!-- Free FD: Withdrawable Status (Hidden by default) -->
                        <div class="form-group" id="withdrawableField" style="display: none;">
                            <label for="withdrawableStatus">Withdrawable Status</label>
                            <select id="withdrawableStatus" name="withdrawableStatus">
                                <option value="">Select Status</option>
                                <option value="Full" <%= "Full".equals(withdrawableStatus) ? "selected" : "" %>>Full</option>
                                <option value="Partial" <%= "Partial".equals(withdrawableStatus) ? "selected" : "" %>>Partial</option>
                                <option value="No" <%= "No".equals(withdrawableStatus) ? "selected" : "" %>>No</option>
                            </select>
                        </div>

                        <!-- Pledge FD: Pledge Value (Hidden by default) -->
                        <div class="form-group" id="pledgeValueField" style="display: none;">
                            <label for="pledgeValue">Pledge Value (RM)</label>
                            <input type="number" id="pledgeValue" name="pledgeValue" step="0.01" min="0.01" value="<%= pledgeValue %>" placeholder="Enter pledge value">
                        </div>

                        <!-- Pledge FD: Collateral Status (Hidden by default) -->
                        <div class="form-group" id="collateralField" style="display: none;">
                            <label for="collateralStatus">Collateral Status</label>
                            <select id="collateralStatus" name="collateralStatus">
                                <option value="">Select Status</option>
                                <option value="Y" <%= "Y".equals(collateralStatus) ? "selected" : "" %>>Active</option>
                                <option value="N" <%= "N".equals(collateralStatus) ? "selected" : "" %>>Partial</option>
                            </select>
                        </div>
                    </div>

                    <!-- Reminder Section -->
					<div class="reminder-section">
					    <div class="reminder-item">
					        <span class="reminder-label">Get a reminder 7 days before maturity dates</span>
					        <label class="toggle-switch">
					            <input type="checkbox" name="reminderMaturity" id="reminderMaturity" value="on" <%= "on".equals(reminderMaturity) ? "checked" : "" %>>
					            <span class="slider"></span>
					        </label>
					    </div>
					
					    <div class="reminder-item">
					        <span class="reminder-label">Get a reminder for incomplete FD Details</span>
					        <label class="toggle-switch">
					            <input type="checkbox" name="reminderIncomplete" id="reminderIncomplete" value="on" <%= "on".equals(reminderIncomplete) ? "checked" : "" %>>
					            <span class="slider"></span>
					        </label>
					    </div>
					</div>

                    <!-- Action Buttons -->
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Next</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Calculation Modal -->
    <div class="modal-overlay" id="calculationModal">
        <div class="modal-content">
            <button class="modal-close" onclick="closeCalculationModal()">×</button>
            <h2 class="modal-title">Calculations</h2>
            
            <div class="calculation-result">
                <label>Expected Total Profit (RM)</label>
                <input type="text" id="totalProfit" readonly>
            </div>
            
            <div class="calculation-result">
                <label>Expected Total Interest (RM)</label>
                <input type="text" id="totalInterest" readonly>
            </div>
        </div>
    </div>

    <script>
        // Auto-calculate maturity date based on start date and tenure
        function calculateMaturityDate() {
            var startDate = document.getElementById('startDate').value;
            var tenure = parseInt(document.getElementById('tenure').value) || 0;

            if (startDate && tenure > 0) {
                var start = new Date(startDate);
                var originalDay = start.getDate();
                start.setMonth(start.getMonth() + tenure);
                
                if (start.getDate() !== originalDay) {
                    start.setDate(0);
                }
                
                var year = start.getFullYear();
                var month = String(start.getMonth() + 1).padStart(2, '0');
                var day = String(start.getDate()).padStart(2, '0');
                
                document.getElementById('maturityDate').value = year + '-' + month + '-' + day;
            }
        }

        // Show notification function
        function showNotification(message, isTurnedOn) {
            const existingNotification = document.querySelector('.notification');
            if (existingNotification) {
                existingNotification.remove();
            }

            const notification = document.createElement('div');
            notification.className = 'notification';
            notification.textContent = message;
            
            if (isTurnedOn) {
                notification.classList.add('turned-on');
            } else {
                notification.classList.add('turned-off');
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

        // Handle FD Type change - show/hide conditional fields
        function handleFDTypeChange() {
            const fdType = document.getElementById('fdType').value;
            
            const autoRenewalField = document.getElementById('autoRenewalField');
            const withdrawableField = document.getElementById('withdrawableField');
            const pledgeValueField = document.getElementById('pledgeValueField');
            const collateralField = document.getElementById('collateralField');
            
            const autoRenewalStatus = document.getElementById('autoRenewalStatus');
            const withdrawableStatus = document.getElementById('withdrawableStatus');
            const pledgeValue = document.getElementById('pledgeValue');
            const collateralStatus = document.getElementById('collateralStatus');
            
            // Hide all conditional fields first
            autoRenewalField.style.display = 'none';
            withdrawableField.style.display = 'none';
            pledgeValueField.style.display = 'none';
            collateralField.style.display = 'none';
            
            // Remove required attribute from all conditional fields
            autoRenewalStatus.removeAttribute('required');
            withdrawableStatus.removeAttribute('required');
            pledgeValue.removeAttribute('required');
            collateralStatus.removeAttribute('required');
            
            // Show relevant fields based on FD Type
            if (fdType === 'Free') {
                autoRenewalField.style.display = 'flex';
                withdrawableField.style.display = 'flex';
                autoRenewalStatus.setAttribute('required', 'required');
                withdrawableStatus.setAttribute('required', 'required');
            } else if (fdType === 'Pledge') {
                pledgeValueField.style.display = 'flex';
                collateralField.style.display = 'flex';
                pledgeValue.setAttribute('required', 'required');
                collateralStatus.setAttribute('required', 'required');
            }
        }

        // Toggle reminder handlers
        document.getElementById('reminderMaturity').addEventListener('change', function() {
            if (this.checked) {
                showNotification('Fixed Deposit Maturity Reminder turned on!', true);
            } else {
                showNotification('Fixed Deposit Maturity Reminder turned off!', false);
            }
        });

        document.getElementById('reminderIncomplete').addEventListener('change', function() {
            if (this.checked) {
                showNotification('Incomplete Fixed Deposit Details Reminder turned on!', true);
            } else {
                showNotification('Incomplete Fixed Deposit Details Reminder turned off!', false);
            }
        });

        // File upload display
        document.getElementById('fdCertificate').addEventListener('change', function(e) {
            const fileName = e.target.files[0]?.name || 'Choose File';
            const fileText = document.getElementById('fileText');
            if (fileName !== 'Choose File') {
                fileText.textContent = fileName;
            }
        });

        // Calculate interest and show modal
        function calculateInterest() {
            const depositAmount = parseFloat(document.getElementById('depositAmount').value) || 0;
            const interestRate = parseFloat(document.getElementById('interestRate').value) || 0;
            const tenure = parseFloat(document.getElementById('tenure').value) || 0;

            if (depositAmount && interestRate && tenure) {
                const interest = (depositAmount * interestRate * tenure) / (100 * 12);
                const totalAmount = depositAmount + interest;
                
                document.getElementById('totalProfit').value = totalAmount.toFixed(2);
                document.getElementById('totalInterest').value = interest.toFixed(2);
                document.getElementById('calculationModal').classList.add('show');
            } else {
                alert('Please fill in Deposit Amount, Interest Rate, and Tenure to calculate.');
            }
        }

        // Close calculation modal
        function closeCalculationModal() {
            document.getElementById('calculationModal').classList.remove('show');
        }

        // Close modal when clicking outside
        document.getElementById('calculationModal').addEventListener('click', function(e) {
            if (e.target === this) {
                closeCalculationModal();
            }
        });

        // Form validation before submit
        document.getElementById('createFDForm').addEventListener('submit', function(e) {
            if (!this.checkValidity()) {
                e.preventDefault();
                this.reportValidity();
                return false;
            }
            
            const startDate = new Date(document.getElementById('startDate').value);
            const maturityDate = new Date(document.getElementById('maturityDate').value);
            
            if (maturityDate < startDate) {
                e.preventDefault();
                alert('Maturity Date must be after Start Date');
                return false;
            }
            
            return true;
        });

        // Initialize on page load
        document.addEventListener('DOMContentLoaded', function() {
            // Auto-expand Fixed Deposits dropdown in sidebar
            const fdDropdown = document.getElementById('fdDropdown');
            const fdNavItem = document.getElementById('fdNavItem');
            
            if (fdDropdown && fdNavItem) {
                fdDropdown.classList.add('show');
                fdNavItem.classList.add('open');
            }
            
            // Show FD Type conditional fields if value is pre-selected (from session)
            const fdType = document.getElementById('fdType').value;
            if (fdType) {
                handleFDTypeChange();
            }
            
            // Show file name if previously uploaded
            <% String fdCertFileName = (String) session.getAttribute("fdCertFileName"); %>
            <% if (fdCertFileName != null && !fdCertFileName.isEmpty()) { %>
                document.getElementById('fileText').textContent = '<%= fdCertFileName %>';
            <% } %>
        });
    </script>
</body>
</html>
