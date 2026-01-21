<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fd.model.FixedDepositRecord" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    // Get FD data from request (set by ViewFDServlet)
    FixedDepositRecord fd = (FixedDepositRecord) request.getAttribute("fd");
    
    // If no FD data, redirect to list
    if (fd == null) {
        response.sendRedirect("FDListServlet");
        return;
    }
    
    // Format dates for display
    SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String startDateStr = fd.getStartDate() != null ? displayDateFormat.format(fd.getStartDate()) : "-";
    String maturityDateStr = fd.getMaturityDate() != null ? displayDateFormat.format(fd.getMaturityDate()) : "-";
    
    // Format numbers
    DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    String depositAmountStr = fd.getDepositAmount() != null ? currencyFormat.format(fd.getDepositAmount()) : "-";
    String interestRateStr = fd.getInterestRate() != null ? fd.getInterestRate().toPlainString() : "-";
    
    // Get type-specific data from request attributes
    String autoRenewalStatus = request.getAttribute("autoRenewalStatus") != null ? (String) request.getAttribute("autoRenewalStatus") : "-";
    String withdrawableStatus = request.getAttribute("withdrawableStatus") != null ? (String) request.getAttribute("withdrawableStatus") : "-";
    String collateralStatus = request.getAttribute("collateralStatus") != null ? (String) request.getAttribute("collateralStatus") : "-";
    BigDecimal pledgeValueBD = request.getAttribute("pledgeValue") != null ? (BigDecimal) request.getAttribute("pledgeValue") : null;
    String pledgeValueStr = pledgeValueBD != null ? currencyFormat.format(pledgeValueBD) : "-";
    
    // Get calculated values
    String totalProfit = request.getAttribute("totalProfit") != null ? (String) request.getAttribute("totalProfit") : "-";
    String totalInterest = request.getAttribute("totalInterest") != null ? (String) request.getAttribute("totalInterest") : "-";
    
    // Convert fdType for display
    String fdTypeDisplay = "FREEFD".equals(fd.getFdType()) ? "Free" : "Pledge";
    
    // Convert status for display
    String statusDisplay = fd.getStatus() != null ? 
        fd.getStatus().substring(0, 1).toUpperCase() + fd.getStatus().substring(1).toLowerCase() : "-";
    
    // Check if status is Matured (for showing transaction section)
    boolean isMatured = "Matured".equalsIgnoreCase(statusDisplay);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Fixed Deposit FD<%= fd.getFdID() %> - Fixed Deposit Tracking System</title>
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
            font-size: 1.8rem;
            color: #2c3e50;
            font-weight: 600;
        }

        .user-profile {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .user-info { text-align: right; }
        .user-name { font-weight: 600; color: #2c3e50; font-size: 16px; }
        .user-role { font-size: 13px; color: #7f8c8d; }

        .user-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #d0d0d0;
        }

        .user-avatar img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            object-fit: cover;
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
        }

        .back-button:hover { color: #2c3e50; }
        .back-icon { font-size: 24px; }

        .page-content {
            padding: 30px 40px;
            margin-top: 35px;
            flex: 1;
        }

        .form-container {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }

        .section-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 25px;
            margin-bottom: 25px;
        }

        .section-box {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
            border: 1px solid #e0e0e0;
        }

        .section-title {
            font-size: 14px;
            font-weight: 600;
            color: #1a4d5e;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #1a4d5e;
        }

        .form-row {
            display: flex;
            align-items: center;
            margin-bottom: 12px;
        }

        .form-row:last-child { margin-bottom: 0; }

        .form-row label {
            font-size: 13px;
            font-weight: 500;
            color: #2c3e50;
            width: 260px;
            flex-shrink: 0;
        }

        .form-row .form-value {
            flex: 1;
            padding: 8px 12px;
            border: 1px solid #d0d0d0;
            border-radius: 6px;
            font-size: 13px;
            font-family: 'Inter', sans-serif;
            background-color: #f0f0f0;
            color: #2c3e50;
        }

        .radio-display {
            display: flex;
            gap: 30px;
            flex: 1;
        }

        .radio-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .radio-item input[type="radio"] {
            width: 18px;
            height: 18px;
            accent-color: #1a4d5e;
            pointer-events: none;
        }

        .radio-item label {
            width: auto;
            font-size: 13px;
        }

        .file-display {
            flex: 1;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .file-display-box {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 12px;
            background: #f0f0f0;
            border: 1px solid #d0d0d0;
            border-radius: 6px;
            font-size: 13px;
            flex: 1;
        }

        .file-icon { font-size: 18px; color: #7f8c8d; }
        .file-name { color: #2c3e50; font-size: 13px; }
        .file-info { font-size: 11px; color: #7f8c8d; }

        .conditional-fields {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px dashed #d0d0d0;
        }

        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 15px;
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
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

        .btn-edit {
            background: #1a4d5e;
            color: white;
        }

        .btn-edit:hover {
            background: #153d4a;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(26, 77, 94, 0.3);
        }

        .btn-print {
            background: #003f5c;
            color: white;
        }

        .btn-print:hover {
            background: #002d42;
        }


        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #5a6268;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(108, 117, 125, 0.3);
        }
        .btn-download {
            background: #003f5c;
            color: white;
        }

        .btn-download:hover {
            background: #002d42;
        }

        /* Status Badge */
        .status-badge {
            padding: 4px 12px;
            border-radius: 15px;
            font-size: 12px;
            font-weight: 500;
            display: inline-block;
        }

        .status-badge.pending {
            background: #ffe5e5;
            color: #c0392b;
        }

        .status-badge.ongoing {
            background: #fff9e5;
            color: #d68910;
        }

        .status-badge.matured {
            background: #e5f5f0;
            color: #0c7a5a;
        }

        @media print {
            .sidebar, .header, .form-actions, .back-button {
                display: none !important;
            }
            
            .main-content {
                margin-left: 0;
            }

            .form-container {
                box-shadow: none;
                border: 1px solid #e0e0e0;
            }

            body {
                background: white;
            }
        }

        @media (max-width: 1200px) {
            .section-grid { grid-template-columns: 1fr; }
        }

        @media (max-width: 768px) {
            .main-content { margin-left: 200px; }
            .header { padding: 15px 20px; }
            .page-content { padding: 20px; }
            .form-row { flex-direction: column; align-items: flex-start; }
            .form-row label { width: 100%; margin-bottom: 5px; }
        }
    </style>
</head>
<body>
    <%@ include file="includes/sidebar.jsp" %>

    <div class="main-content">
        <% request.setAttribute("pageTitle", "View Fixed Deposit"); %>
		<%@ include file="includes/HeaderInclude.jsp" %>

        <div class="page-content">
            <a href="FDListServlet" class="back-button">
                <span class="back-icon">←</span>
                <span>Back</span>
            </a>

            <div class="form-container">
                <!-- Row 1: Reference & Type -->
                <div class="section-grid">
                    <div class="section-box">
                        <div class="section-title">Fixed Deposit Reference</div>
                        <div class="form-row">
                            <label>Fixed Deposit ID</label>
                            <div class="form-value">FD<%= fd.getFdID() %></div>
                        </div>
                        <div class="form-row">
                            <label>Account Number</label>
                            <div class="form-value"><%= fd.getAccNumber() != null ? fd.getAccNumber() : "-" %></div>
                        </div>
                        <div class="form-row">
                            <label>Referral Number</label>
                            <div class="form-value"><%= fd.getReferralNumber() != null ? fd.getReferralNumber().toPlainString() : "-" %></div>
                        </div>
                    </div>

                    <div class="section-box">
                        <div class="section-title">Fixed Deposit Type</div>
                        <div class="form-row">
                            <label>FD Type</label>
                            <div class="radio-display">
                                <div class="radio-item">
                                    <input type="radio" id="typePledge" name="fdType" value="Pledge" <%= "Pledge".equals(fdTypeDisplay) ? "checked" : "" %> disabled>
                                    <label for="typePledge">Pledge FD</label>
                                </div>
                                <div class="radio-item">
                                    <input type="radio" id="typeFree" name="fdType" value="Free" <%= "Free".equals(fdTypeDisplay) ? "checked" : "" %> disabled>
                                    <label for="typeFree">Free FD</label>
                                </div>
                            </div>
                        </div>

                        <!-- Pledge FD Fields -->
                        <div id="pledgeFields" class="conditional-fields" style="display: <%= "Pledge".equals(fdTypeDisplay) ? "block" : "none" %>;">
                            <div class="form-row">
                                <label>Collateral Status</label>
                                <div class="form-value"><%= collateralStatus %></div>
                            </div>
                            <div class="form-row">
                                <label>Pledge Value (RM)</label>
                                <div class="form-value"><%= pledgeValueStr %></div>
                            </div>
                        </div>

                        <!-- Free FD Fields -->
                        <div id="freeFields" class="conditional-fields" style="display: <%= "Free".equals(fdTypeDisplay) ? "block" : "none" %>;">
                            <div class="form-row">
                                <label>Auto Renewal Status</label>
                                <div class="form-value"><%= autoRenewalStatus %></div>
                            </div>
                            <div class="form-row">
                                <label>Withdrawable Status</label>
                                <div class="form-value"><%= withdrawableStatus %></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Row 2: Details -->
                <div class="section-box full-width" style="margin-bottom: 25px;">
                    <div class="section-title">Fixed Deposit Details</div>
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">
                        <div>
                            <div class="form-row">
                                <label>Bank</label>
                                <div class="form-value"><%= fd.getBankName() != null ? fd.getBankName() : "-" %></div>
                            </div>
                            <div class="form-row">
                                <label>Start Date</label>
                                <div class="form-value"><%= startDateStr %></div>
                            </div>
                            <div class="form-row">
                                <label>Maturity Date</label>
                                <div class="form-value"><%= maturityDateStr %></div>
                            </div>
                            <div class="form-row">
                                <label>Interest Rate (%)</label>
                                <div class="form-value"><%= interestRateStr %></div>
                            </div>
                        </div>
                        <div>
                            <div class="form-row">
                                <label>Deposit Amount (RM)</label>
                                <div class="form-value"><%= depositAmountStr %></div>
                            </div>
                            <div class="form-row">
                                <label>Tenure (Months)</label>
                                <div class="form-value"><%= fd.getTenure() %></div>
                            </div>
                            <div class="form-row">
                                <label>Status</label>
                                <div class="form-value"><%= statusDisplay %> </div>
                            </div>
                            <div class="form-row">
                                <label>Created By</label>
                                <div class="form-value">Admin</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Row 3: Transaction & Certification -->
                <div class="section-grid">
                    <!-- Row 4: Calculation Results -->
                    <div class="section-box">
                        <div class="section-title">Expected Calculations</div>
                        <div class="form-row">
                            <label>Expected Total Profit (RM)</label>
                            <div class="form-value"><%= totalProfit %></div>
                        </div>
                        <div class="form-row">
                            <label>Expected Total Interest (RM)</label>
                            <div class="form-value"><%= totalInterest %></div>
                        </div>
                    </div>


                    <!-- Certification -->
                    <div class="section-box">
                        <div class="section-title">Fixed Deposit Certification</div>
                        <div class="form-row">
                            <label>Certificate No.</label>
                            <div class="form-value"><%= fd.getCertNo() != null ? fd.getCertNo() : "-" %></div>
                        </div>
                        <div class="form-row">
                            <label>FD Certificate</label>
                            <div class="file-display">
                                <div class="file-display-box">
                                    <span class="file-icon">📁</span>
                                    <span class="file-name"><%= fd.getCertNo() != null ? "Certificate uploaded" : "No file" %></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Transaction Section - Only show if Matured -->
                    <div class="section-box" id="transactionSection" style="display: <%= isMatured ? "block" : "none" %>;">
                        <div class="section-title">Fixed Deposit Transaction</div>
                        
                        <!-- Balance Summary for Matured FDs -->
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px; margin-bottom: 15px;">
                            <div>
                                <div style="font-size: 12px; color: #666; margin-bottom: 5px;">💰 Remaining Balance</div>
                                <div style="font-size: 15px; font-weight: 600; color: #1a4d5e;">
                                    RM <%= fd.getRemainingBalance() != null ? String.format("%,.2f", fd.getRemainingBalance()) : "0.00" %>
                                </div>
                            </div>
                            <div>
                                <div style="font-size: 12px; color: #666; margin-bottom: 5px;">📤 Total Withdrawn</div>
                                <div style="font-size: 15px; font-weight: 600; color: #1a4d5e;">
                                    RM <%= fd.getTotalWithdrawn() != null ? String.format("%,.2f", fd.getTotalWithdrawn()) : "0.00" %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                

                <!-- Action Buttons -->
                <div class="form-actions">
                    <button type="button" class="btn btn-edit" onclick="editFD()">Edit</button>
                    <button type="button" class="btn btn-secondary" onclick="viewApplicationForm()">View Application Form</button>
                    <button type="button" class="btn btn-print" onclick="window.print()">Print</button>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Edit FD - redirect to UpdateFDServlet
        function editFD() {
            window.location.href = 'UpdateFDServlet?id=<%= fd.getFdID() %>';
        }
        // View Application Form
        function viewApplicationForm() {
            window.location.href = 'ViewApplicationServlet?id=<%= fd.getFdID() %>';
        }


        // Initialize
        document.addEventListener('DOMContentLoaded', function() {
            // Auto-expand Fixed Deposits menu
            const fdDropdown = document.getElementById('fdDropdown');
            const fdNavItem = document.getElementById('fdNavItem');
            if (fdDropdown && fdNavItem) {
                fdDropdown.classList.add('show');
                fdNavItem.classList.add('open');
            }
        });
    </script>
</body>
</html>
