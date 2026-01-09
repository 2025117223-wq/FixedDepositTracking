<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

        .action-icon.delete {
            color: #e74c3c;
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

        /* Success Message */
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
            margin-left: 100px;
            margin-top: -50px;
            opacity: 0;
            display: block;
        }
        
        /* Show animation */
        .success-message.show {
            animation: slideDown 0.4s ease forwards;
        }
        
        /* Hide animation */
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

        /* Modal Styles */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 9999;
            justify-content: center;
            align-items: center;
        }

        .modal-overlay.active {
            display: flex;
        }

        .modal-content {
            background: white;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
            text-align: center;
            min-width: 400px;
        }

        .modal-message {
            font-size: 16px;
            color: #2c3e50;
            margin-bottom: 30px;
            line-height: 1.5;
        }

        .modal-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
        }

        .modal-btn {
            padding: 12px 40px;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            font-family: 'Inter', sans-serif;
        }

        .modal-btn-no {
            background: #95a5a6;
            color: white;
        }

        .modal-btn-no:hover {
            background: #7f8c8d;
        }

        .modal-btn-yes {
            background: #003f5c;
            color: white;
        }

        .modal-btn-yes:hover {
            background: #002d42;
        }

        .modal-btn-delete {
            background: #e74c3c;
            color: white;
        }

        .modal-btn-delete:hover {
            background: #c0392b;
        }

        @media (max-width: 768px) {
            .main-content {
                margin-left: 200px;
            }

            .page-content {
                padding: 20px;
            }

            th, td {
                padding: 15px 10px;
                font-size: 14px;
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
                    <div class="user-name">Nor Azlina</div>
                    <div class="user-role">Administrator</div>
                </div>
                <div class="user-avatar">
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
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="bankTableBody">
                        <!-- Banks will be inserted here by JavaScript -->
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Success Message -->
    <div class="success-message" id="successMessage"></div>

    <!-- Update Confirmation Modal -->
    <div class="modal-overlay" id="updateModal">
        <div class="modal-content">
            <div class="modal-message" id="updateModalMessage">
                Are you sure you want to update this bank?
            </div>
            <div class="modal-buttons">
                <button class="modal-btn modal-btn-no" onclick="closeUpdateModal()">No</button>
                <button class="modal-btn modal-btn-yes" onclick="confirmUpdate()">Yes</button>
            </div>
        </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal-overlay" id="deleteModal">
        <div class="modal-content">
            <div class="modal-message" id="deleteModalMessage">
                Are you sure you want to delete this bank?
            </div>
            <div class="modal-buttons">
                <button class="modal-btn modal-btn-no" onclick="closeDeleteModal()">No</button>
                <button class="modal-btn modal-btn-delete" onclick="confirmDelete()">Yes</button>
            </div>
        </div>
    </div>

    <script>
        // Sample bank data (in real implementation, this would come from database/servlet)
        const banks = [
            { id: 'MB0234', name: 'Maybank' },
            { id: 'CB9562', name: 'CIMB' },
            { id: 'MS1234', name: 'MBSB' },
            { id: 'MS8542', name: 'RHB' },
            { id: 'MB9631', name: 'Bank Islam' },
            { id: 'MB7584', name: 'Bank Rakyat' }
        ];

        // Auto-expand Bank dropdown in sidebar
        document.addEventListener('DOMContentLoaded', function() {
            const bankDropdown = document.getElementById('bankDropdown');
            const bankNavItem = document.getElementById('bankNavItem');
            
            if (bankDropdown && bankNavItem) {
                bankDropdown.classList.add('show');
                bankNavItem.classList.add('open');
            }

            // Load banks into table
            loadBanks();

            // Check if coming from Create Bank page
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('success') === 'created') {
                showSuccessMessage('New bank added successfully!');
                
                // Clear the URL parameter after showing message
                window.history.replaceState({}, document.title, 'BankList.jsp');
            }
        });

        function loadBanks() {
            const tbody = document.getElementById('bankTableBody');
            tbody.innerHTML = '';

            if (banks.length === 0) {
                tbody.innerHTML = '<tr><td colspan="3" class="no-data">No banks found. Add a new bank to get started.</td></tr>';
                return;
            }

            banks.forEach(bank => {
                const row = document.createElement('tr');
                row.innerHTML = 
                    '<td>' + bank.id + '</td>' +
                    '<td>' + bank.name + '</td>' +
                    '<td>' +
                        '<div class="action-buttons">' +
                            '<div class="action-btn" onclick="updateBank(\'' + bank.id + '\', \'' + bank.name + '\')">' +
                                '<div class="action-icon update">✏️</div>' +
                                '<div class="action-label">Update</div>' +
                            '</div>' +
                            '<div class="action-btn" onclick="deleteBank(\'' + bank.id + '\', \'' + bank.name + '\')">' +
                                '<div class="action-icon delete">🗑️</div>' +
                                '<div class="action-label">Delete</div>' +
                            '</div>' +
                        '</div>' +
                    '</td>';
                tbody.appendChild(row);
            });
        }

        function updateBank(bankId, bankName) {
            // Store current update info
            window.currentUpdateBank = { id: bankId, name: bankName };
            
            // Show update modal
            document.getElementById('updateModalMessage').textContent = 
                'Are you sure you want to update "' + bankName + '"?';
            document.getElementById('updateModal').classList.add('active');
        }

        function closeUpdateModal() {
            document.getElementById('updateModal').classList.remove('active');
            window.currentUpdateBank = null;
        }

        function confirmUpdate() {
            const bankInfo = window.currentUpdateBank;
            if (!bankInfo) return;
            
            closeUpdateModal();
            
            // Show prompt for new name
            const newName = prompt('Enter new bank name:', bankInfo.name);
            if (newName && newName.trim() !== '' && newName !== bankInfo.name) {
                // Find and update the bank
                const bank = banks.find(b => b.id === bankInfo.id);
                if (bank) {
                    bank.name = newName.trim();
                    loadBanks();
                    showSuccessMessage('Bank updated successfully!');
                }
            }
        }

        function deleteBank(bankId, bankName) {
            // Store current delete info
            window.currentDeleteBank = { id: bankId, name: bankName };
            
            // Show delete modal
            document.getElementById('deleteModalMessage').textContent = 
                'Are you sure you want to delete "' + bankName + '"?';
            document.getElementById('deleteModal').classList.add('active');
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').classList.remove('active');
            window.currentDeleteBank = null;
        }

        function confirmDelete() {
            const bankInfo = window.currentDeleteBank;
            if (!bankInfo) return;
            
            closeDeleteModal();
            
            // Remove from array
            const index = banks.findIndex(b => b.id === bankInfo.id);
            if (index > -1) {
                banks.splice(index, 1);
                loadBanks();
                showSuccessMessage('Bank deleted successfully!');
            }
        }

        function showSuccessMessage(message) {
            const successMsg = document.getElementById('successMessage');
            successMsg.textContent = message;
            
            // Add show class to trigger slideDown animation
            successMsg.classList.add('show');
            successMsg.classList.remove('hide');

            // After 3 seconds, hide with animation
            setTimeout(function() {
                successMsg.classList.remove('show');
                successMsg.classList.add('hide');
                
                // Remove hide class after animation completes (400ms)
                setTimeout(function() {
                    successMsg.classList.remove('hide');
                }, 400);
            }, 3000);
        }
    </script>
</body>
</html>
