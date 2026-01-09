<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Users Lists - Fixed Deposit Tracking System</title>
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

        .search-container {
            margin-bottom: 30px;
            max-width: 500px;
        }

        .search-box {
            position: relative;
            width: 100%;
        }

        .search-input {
            width: 100%;
            padding: 15px 50px 15px 20px;
            border: 2px solid #d0d0d0;
            border-radius: 10px;
            font-size: 15px;
            font-family: 'Inter', sans-serif;
            transition: all 0.3s ease;
        }

        .search-input:focus {
            outline: none;
            border-color: #003f5c;
        }

        .search-input::placeholder {
            color: #a0a0a0;
        }

        .search-icon {
            position: absolute;
            right: 20px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 20px;
            color: #7f8c8d;
            pointer-events: none;
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
            <h1>Users Lists</h1>
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
            <div class="search-container">
                <div class="search-box">
                    <input 
                        type="text" 
                        class="search-input" 
                        id="searchInput" 
                        placeholder="Search"
                        onkeyup="searchUsers()"
                    >
                    <span class="search-icon">🔍</span>
                </div>
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>Staff ID</th>
                            <th>Name</th>
                            <th>Roles</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="userTableBody">
                        <!-- Users will be inserted here by JavaScript -->
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
                Are you sure you want to update this user?
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
                Are you sure you want to delete this user?
            </div>
            <div class="modal-buttons">
                <button class="modal-btn modal-btn-no" onclick="closeDeleteModal()">No</button>
                <button class="modal-btn modal-btn-delete" onclick="confirmDelete()">Yes</button>
            </div>
        </div>
    </div>

    <script>
        // Sample user data (in real implementation, this would come from database/servlet)
        const users = [
            { staffId: 'FinanceE02', name: 'Aufa Haniz Binti Khairuol Rin', role: 'Finance Executive' },
            { staffId: 'FinanceS01', name: 'Nabella Ameliya Binti Fairuz', role: 'Finance Staff' },
            { staffId: 'FinanceS02', name: 'Agus Syafiah Qaisara Binti Ikhwan Meon', role: 'Finance Staff' },
            { staffId: 'FinanceS03', name: 'Khairina Izzaty Binti Md Nor Azim', role: 'Finance Staff' },
            { staffId: 'FinanceS04', name: 'Aidil Danial Bin Badrul Khan', role: 'Finance Staff' },
            { staffId: 'FinanceS05', name: 'Nor Tamia Shaheerah Binti Zakrul Zain', role: 'Finance Staff' },
            { staffId: 'FinanceE03', name: 'Muhammad Luqman Ikmal Bin Rahmad', role: 'Finance Executive' }
        ];

        let filteredUsers = [...users]; // Copy of users for filtering

        document.addEventListener('DOMContentLoaded', function() {
            // Load users into table
            loadUsers(users);
        });

        function loadUsers(userList) {
            const tbody = document.getElementById('userTableBody');
            tbody.innerHTML = '';

            if (userList.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" class="no-data">No users found.</td></tr>';
                return;
            }

            userList.forEach(user => {
                const row = document.createElement('tr');
                row.innerHTML = 
                    '<td>' + user.staffId + '</td>' +
                    '<td>' + user.name + '</td>' +
                    '<td>' + user.role + '</td>' +
                    '<td>' +
                        '<div class="action-buttons">' +
                            '<div class="action-btn" onclick="updateUser(\'' + user.staffId + '\')">' +
                                '<div class="action-icon update">✏️</div>' +
                                '<div class="action-label">Update</div>' +
                            '</div>' +
                            '<div class="action-btn" onclick="deleteUser(\'' + user.staffId + '\', \'' + user.name + '\')">' +
                                '<div class="action-icon delete">🗑️</div>' +
                                '<div class="action-label">Delete</div>' +
                            '</div>' +
                        '</div>' +
                    '</td>';
                tbody.appendChild(row);
            });
        }

        function searchUsers() {
            const searchTerm = document.getElementById('searchInput').value.toLowerCase();
            
            const filtered = users.filter(user => {
                return user.staffId.toLowerCase().includes(searchTerm) ||
                       user.name.toLowerCase().includes(searchTerm) ||
                       user.role.toLowerCase().includes(searchTerm);
            });

            loadUsers(filtered);
        }

        function updateUser(staffId) {
            // Find the user
            const user = users.find(u => u.staffId === staffId);
            if (!user) return;
            
            // Store current update info
            window.currentUpdateUser = user;
            
            // Show update modal
            document.getElementById('updateModalMessage').textContent = 
                'Are you sure you want to update "' + user.name + '"?';
            document.getElementById('updateModal').classList.add('active');
        }

        function closeUpdateModal() {
            document.getElementById('updateModal').classList.remove('active');
            window.currentUpdateUser = null;
        }

        function confirmUpdate() {
            const user = window.currentUpdateUser;
            if (!user) return;
            
            closeUpdateModal();
            
            // In real implementation, redirect to update page
            // window.location.href = 'UpdateUser.jsp?id=' + user.staffId;
            
            showSuccessMessage('Redirecting to update page...');
        }

        function deleteUser(staffId, name) {
            // Store current delete info
            window.currentDeleteUser = { staffId: staffId, name: name };
            
            // Show delete modal
            document.getElementById('deleteModalMessage').textContent = 
                'Are you sure you want to delete "' + name + '"?';
            document.getElementById('deleteModal').classList.add('active');
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').classList.remove('active');
            window.currentDeleteUser = null;
        }

        function confirmDelete() {
            const userInfo = window.currentDeleteUser;
            if (!userInfo) return;
            
            closeDeleteModal();
            
            // In real implementation, call servlet/API to delete
            
            // Remove from array
            const index = users.findIndex(u => u.staffId === userInfo.staffId);
            if (index > -1) {
                users.splice(index, 1);
                
                // Reload current search results
                searchUsers();
                
                showSuccessMessage('User deleted successfully!');
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
