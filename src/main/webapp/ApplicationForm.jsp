<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Fixed Deposit Application Form</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
		    font-family: 'Inter', sans-serif;  /* ✅ Same as other pages */
		    font-size: 14px;
		    line-height: 1.5;
		}
		
		/* Document specific styles - only for the printed document */
		.document-container {
		    font-family: Calibri, Arial, sans-serif;  /* Calibri only for the document itself */
		    font-size: 11pt;
		    line-height: 1.4;
		}

        /* Screen Layout */
        .screen-wrapper {
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
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .header h1 {
            font-size: 2rem;
            color: #2c3e50;
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
            display: flex;
            justify-content: center;
        }

        .document-container {
            background: white;
            width: 100%;
            max-width: 21cm;
            padding: 3cm 2.5cm;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        /* Document Styles */
        .doc-section {
            margin-bottom: 1.5em;
        }

        .ref-line {
            margin-bottom: 0.3em;
        }

        .bold {
            font-weight: 700;
        }

        .red {
            color: #C00000;
            font-weight: 700;
        }

        .underline {
            text-decoration: underline;
            font-weight: 700;
        }

        .title-line {
            border-bottom: 1.5pt solid #000;
            padding-bottom: 3px;
            margin-top: 3px;
        }

        .info-table {
            width: 75%;
            border-collapse: collapse;
            border: 1.5pt solid #000;
            margin: 1em 0;
        }

        .info-table td {
            border: 1pt solid #000;
            padding: 0.4em 0.8em;
        }

        .info-table .label {
            width: 35%;
        }

        .info-table .colon {
            width: 5%;
            text-align: center;
        }

        .signature-area {
            margin-top: 3em;
        }

        .signature-lines {
            display: flex;
            justify-content: space-between;
            margin-top: 5em;
            gap: 8em;
        }

        .sig-line {
            border-top: 1.5pt solid #000;
            padding-top: 0.3em;
            min-width: 150px;
        }

        .form-actions {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-top: 40px;
            padding-top: 30px;
            border-top: 2px solid #e0e0e0;
        }

        .btn {
            padding: 15px 50px;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn-print {
            background: #003f5c;
            color: white;
        }

        .btn-print:hover {
            background: #002d42;
        }

        .btn-submit {
            background: white;
            color: #2c3e50;
            border: 2px solid #2c3e50;
        }

        .btn-submit:hover {
            background: #2c3e50;
            color: white;
        }

        .btn-back {
            background: #e0e0e0;
            color: #2c3e50;
        }

        .btn-back:hover {
            background: #d0d0d0;
        }

        /* Confirmation Modal */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            z-index: 9999;
            justify-content: center;
            align-items: center;
        }

        .modal-overlay.show {
            display: flex;
        }

        .modal-content {
            background: white;
            border-radius: 15px;
            padding: 40px 50px;
            max-width: 500px;
            width: 90%;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
            text-align: center;
        }

        .modal-message {
            font-size: 18px;
            color: #2c3e50;
            margin-bottom: 30px;
            font-weight: 500;
        }

        .modal-buttons {
            display: flex;
            justify-content: center;
            gap: 15px;
        }

        .modal-btn {
            padding: 12px 40px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .modal-btn-no {
            background: #f0f0f0;
            color: #2c3e50;
            border: 2px solid #d0d0d0;
        }

        .modal-btn-no:hover {
            background: #e0e0e0;
        }

        .modal-btn-yes {
            background: #003f5c;
            color: white;
        }

        .modal-btn-yes:hover {
            background: #002d42;
        }

        /* Success Message */
        .success-message {
            position: fixed;
            top: 100px;
            left: 50%;
            transform: translateX(-50%);
            background: #7dd3c0;
            color: #2c3e50;
            padding: 15px 40px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 500;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            z-index: 10000;
            display: none;
            animation: slideDown 0.3s ease;
        }

        .success-message.show {
            display: block;
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

        /* Print Styles */
        @media print {
            @page {
                size: A4;
                margin: 2cm;
            }

            body {
                background: white;
            }

            .screen-wrapper {
                display: none !important;
            }

            .sidebar,
            .header,
            .form-actions,
            .page-content,
            .main-content {
                display: none !important;
            }

            .print-only {
                display: block !important;
            }

            .document-container {
                box-shadow: none;
                padding: 0.5cm;
                max-width: 100%;
                width: 100%;
                display: block !important;
                page-break-before: avoid;
            }

            * {
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }
        }

        @media screen {
            .print-only {
                display: none;
            }
        }
    </style>
</head>
<body>
    <div class="screen-wrapper">
    <%@ include file="includes/sidebar.jsp" %>
        <div class="main-content">
            <div class="header screen-only">
                <h1>Fixed Deposit Application Form</h1>
                <div class="user-profile">
                    <div class="user-info">
                        <div class="user-name">Nor Azlina</div>
                        <div class="user-role">Administrator</div>
                    </div>
                    <div class="user-avatar">
                        <img src="images/icons/user.jpg" alt="User" onerror="this.style.display='none'">
                    </div>
                </div>
            </div>

            <div class="page-content screen-only">
                <div class="document-container">
                    <div id="printable-content">
                        <!-- Content -->
                    </div>
                    
                    <div class="form-actions">
                        <button class="btn btn-back" onclick="window.history.back()">Back</button>
                        <button class="btn btn-print" onclick="window.print()">Print</button>
                        <button class="btn btn-submit" onclick="submitForm()">Submit</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Print Version -->
    <div class="print-only document-container">
        <div id="print-content">
            <!-- Content will be duplicated here -->
        </div>
    </div>

    <!-- Confirmation Modal -->
    <div class="modal-overlay" id="confirmModal">
        <div class="modal-content">
            <div class="modal-message">Are you sure want to submit this information?</div>
            <div class="modal-buttons">
                <button class="modal-btn modal-btn-no" onclick="closeConfirmModal()">No</button>
                <button class="modal-btn modal-btn-yes" onclick="confirmSubmit()">Yes</button>
            </div>
        </div>
    </div>

    <!-- Success Message -->
    <div class="success-message" id="successMessage">
        New Fixed Deposit added successfully!
    </div>

    <script>
        const contentHTML = `
            <div class="ref-line">Our Ref.<span style="margin-left: 2em;">:</span><span style="margin-left: 1.5em;" id="refNumber">IDJSB/HO/FCA/L-FDMBB/2022 ( )</span></div>
            <div class="ref-line">Date<span style="margin-left: 3.2em;">:</span><span style="margin-left: 1.5em;" id="docDate"></span></div>

            <div class="doc-section">
                <div class="bold">The Manager</div>
                <div class="bold" id="bankName">Maybank Islamic Berhad</div>
                <div id="bankAddress1">Cawangan Jalan Gombak,</div>
                <div id="bankAddress2">Selangor.</div>
                <div class="red" id="attentionLine"></div>
            </div>

            <div class="doc-section">Dear Sir/Madam,</div>

            <div class="doc-section">
                <div class="bold">FIXED DEPOSIT (ISLAMIC) PLACEMENT</div>
                <div class="title-line"></div>
            </div>

            <div class="doc-section">The above matters refers.</div>

            <div class="doc-section">Kindly arrange for placement of Fixed Deposit as per details given below:</div>

            <table class="info-table">
                <tr>
                    <td class="label">Amount</td>
                    <td class="colon">:</td>
                    <td id="fdAmount">RM</td>
                </tr>
                <tr>
                    <td class="label">Duration</td>
                    <td class="colon">:</td>
                    <td id="fdDuration">months</td>
                </tr>
                <tr>
                    <td class="label">Profit Rate</td>
                    <td class="colon">:</td>
                    <td id="fdProfitRate">%</td>
                </tr>
            </table>

            <div class="doc-section">Please debit our accounts as per mention below:</div>

            <table class="info-table">
                <tr>
                    <td class="label">Name</td>
                    <td class="colon">:</td>
                    <td>INFRA DESA (JOHOR) SDN BHD</td>
                </tr>
                <tr>
                    <td class="label">Bank</td>
                    <td class="colon">:</td>
                    <td id="accountBank">Maybank Islamic Bank Berhad</td>
                </tr>
                <tr>
                    <td class="label">Account Number</td>
                    <td class="colon">:</td>
                    <td id="accountNumber">5513 4210 3048</td>
                </tr>
            </table>

            <div class="doc-section">Your cooperation on this matter is highly appreciated.</div>

            <div class="doc-section">Thank you.</div>

            <div class="signature-area">
                <div>Yours faithfully,</div>
                <div class="bold">INFRA DESA (JOHOR) SDN. BHD.</div>
            </div>

            <div class="signature-lines">
                <div class="sig-line red">Authorised Signatory</div>
                <div class="sig-line red">Authorised Signatory</div>
            </div>
        `;

        document.getElementById('printable-content').innerHTML = contentHTML;
        document.getElementById('print-content').innerHTML = contentHTML;

        window.addEventListener('DOMContentLoaded', function() {
            const fdData = sessionStorage.getItem('fdData');
            if (fdData) {
                const data = JSON.parse(fdData);
                populateForm(data);
            }
            setDocumentDate();
        });

        function populateForm(data) {
            const elements = ['fdAmount', 'fdDuration', 'fdProfitRate', 'bankName', 'accountBank', 'accountNumber', 'bankAddress1', 'bankAddress2', 'attentionLine'];
            
            if (data.depositAmount) {
                const amount = parseFloat(data.depositAmount).toLocaleString('en-MY', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                });
                setElements('fdAmount', 'RM ' + amount);
            }
            
            if (data.tenure) {
                const monthText = parseInt(data.tenure) === 1 ? 'month' : 'months';
                setElements('fdDuration', data.tenure + ' ' + monthText);
            }
            
            if (data.interestRate) {
                setElements('fdProfitRate', data.interestRate + '%');
            }
            
            if (data.bankName) {
                setElements('bankName', data.bankName);
                setElements('accountBank', data.bankName);
                updateBankAddress(data.bankName);
            }
            
            if (data.accountNumber) {
                setElements('accountNumber', formatAccountNumber(data.accountNumber));
            }
        }

        function setElements(id, value) {
            const screenEl = document.querySelector('#printable-content #' + id);
            const printEl = document.querySelector('#print-content #' + id);
            if (screenEl) screenEl.textContent = value;
            if (printEl) printEl.textContent = value;
        }

        function formatAccountNumber(accountNumber) {
            const cleaned = accountNumber.replace(/\s/g, '');
            const parts = [];
            for (let i = 0; i < cleaned.length; i += 4) {
                parts.push(cleaned.substring(i, i + 4));
            }
            return parts.join(' ');
        }

        function updateBankAddress(bankName) {
            const addresses = {
                'Maybank': {
                    line1: 'Cawangan Jalan Gombak,',
                    line2: 'Selangor.',
                    attention: ''
                },
                'CIMB': {
                    line1: 'Menara CIMB, Jalan Stesen Sentral 2,',
                    line2: 'Kuala Lumpur Sentral,',
                    line3: '50470 Kuala Lumpur.',
                    attention: ''
                }
            };

            const address = addresses[bankName] || addresses['Maybank'];
            setElements('bankAddress1', address.line1);
            setElements('bankAddress2', address.line2);
            
            if (address.attention) {
                setElements('attentionLine', address.attention);
            }
        }

        function setDocumentDate() {
            const today = new Date();
            const day = String(today.getDate()).padStart(2, '0');
            const month = String(today.getMonth() + 1).padStart(2, '0');
            const year = today.getFullYear();
            setElements('docDate', day + '/' + month + '/' + year);
        }

        function submitForm() {
            // Show confirmation modal
            document.getElementById('confirmModal').classList.add('show');
        }

        function closeConfirmModal() {
            document.getElementById('confirmModal').classList.remove('show');
        }

        function confirmSubmit() {
            // Close modal
            closeConfirmModal();
            
            // Clear session storage
            sessionStorage.removeItem('fdData');
            
            // Set success flag in sessionStorage
            sessionStorage.setItem('fdSuccess', 'true');
            
            // Redirect to FD List
            window.location.href = 'FDList.jsp';
        }
    </script>
</body>
</html>
