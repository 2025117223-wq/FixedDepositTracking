<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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

        .action-buttons {
            display: flex;
            gap: 10px;
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

        .action-icon.delete {
            color: #e74c3c;
        }

        .action-label {
            font-size: 11px;
            color: #7f8c8d;
            font-weight: 500;
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
		    pointer-events: none;
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
                    <div class="user-name">Nor Azlina</div>
                    <div class="user-role">Administrator</div>
                </div>
                <div class="user-avatar">
                    <img src="images/icons/user.jpg" alt="User Avatar" onerror="this.style.display='none'">
                </div>
            </div>
        </div>

        <!-- Page Content -->
        <div class="page-content">
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
                        <tr>
                            <td>FD2465</td>
                            <td><a href="#" class="account-link">9876543210123</a></td>
                            <td>25,000</td>
                            <td>Maybank</td>
                            <td>17</td>
                            <td><span class="status pending">Pending</span></td>
                            <td>
                                <div class="action-buttons">
                                    <div class="action-btn" onclick="updateFD('FD2465')">
                                        <div class="action-icon update">✏️</div>
                                        <div class="action-label">Update</div>
                                    </div>
                                    <div class="action-btn" onclick="deleteFD('FD2465')">
                                        <div class="action-icon delete">🗑️</div>
                                        <div class="action-label">Delete</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>FD7895</td>
                            <td><a href="#" class="account-link">5032918476201</a></td>
                            <td>60,000</td>
                            <td>MBSB</td>
                            <td>6</td>
                            <td><span class="status ongoing">Ongoing</span></td>
                            <td>
                                <div class="action-buttons">
                                    <div class="action-btn" onclick="updateFD('FD7895')">
                                        <div class="action-icon update">✏️</div>
                                        <div class="action-label">Update</div>
                                    </div>
                                    <div class="action-btn" onclick="deleteFD('FD7895')">
                                        <div class="action-icon delete">🗑️</div>
                                        <div class="action-label">Delete</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>FD1024</td>
                            <td><a href="#" class="account-link">7648391205784</a></td>
                            <td>50,500</td>
                            <td>CIMB</td>
                            <td>17</td>
                            <td><span class="status ongoing">Ongoing</span></td>
                            <td>
                                <div class="action-buttons">
                                    <div class="action-btn" onclick="updateFD('FD1024')">
                                        <div class="action-icon update">✏️</div>
                                        <div class="action-label">Update</div>
                                    </div>
                                    <div class="action-btn" onclick="deleteFD('FD1024')">
                                        <div class="action-icon delete">🗑️</div>
                                        <div class="action-label">Delete</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>FD7230</td>
                            <td><a href="#" class="account-link">1928374650192</a></td>
                            <td>35,000</td>
                            <td>Maybank</td>
                            <td>17</td>
                            <td><span class="status ongoing">Ongoing</span></td>
                            <td>
                                <div class="action-buttons">
                                    <div class="action-btn" onclick="updateFD('FD7230')">
                                        <div class="action-icon update">✏️</div>
                                        <div class="action-label">Update</div>
                                    </div>
                                    <div class="action-btn" onclick="deleteFD('FD7230')">
                                        <div class="action-icon delete">🗑️</div>
                                        <div class="action-label">Delete</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>FD1126</td>
                            <td><a href="#" class="account-link">6482039174520</a></td>
                            <td>25,000</td>
                            <td>Maybank</td>
                            <td>12</td>
                            <td><span class="status matured">Matured</span></td>
                            <td>
                                <div class="action-buttons">
                                    <div class="action-btn" onclick="updateFD('FD1126')">
                                        <div class="action-icon update">✏️</div>
                                        <div class="action-label">Update</div>
                                    </div>
                                    <div class="action-btn" onclick="deleteFD('FD1126')">
                                        <div class="action-icon delete">🗑️</div>
                                        <div class="action-label">Delete</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>FD4052</td>
                            <td><a href="#" class="account-link">3857204916837</a></td>
                            <td>18,000</td>
                            <td>MBSB</td>
                            <td>11</td>
                            <td><span class="status pending">Pending</span></td>
                            <td>
                                <div class="action-buttons">
                                    <div class="action-btn" onclick="updateFD('FD4052')">
                                        <div class="action-icon update">✏️</div>
                                        <div class="action-label">Update</div>
                                    </div>
                                    <div class="action-btn" onclick="deleteFD('FD4052')">
                                        <div class="action-icon delete">🗑️</div>
                                        <div class="action-label">Delete</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>FD8163</td>
                            <td><a href="#" class="account-link">2179845301629</a></td>
                            <td>47,000</td>
                            <td>CIMB</td>
                            <td>8</td>
                            <td><span class="status matured">Matured</span></td>
                            <td>
                                <div class="action-buttons">
                                    <div class="action-btn" onclick="updateFD('FD8163')">
                                        <div class="action-icon update">✏️</div>
                                        <div class="action-label">Update</div>
                                    </div>
                                    <div class="action-btn" onclick="deleteFD('FD8163')">
                                        <div class="action-icon delete">🗑️</div>
                                        <div class="action-label">Delete</div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
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

        // Update FD function
        function updateFD(fdId) {
            alert('Update FD: ' + fdId);
            // Redirect to update page or open modal
            // window.location.href = 'UpdateFD.jsp?id=' + fdId;
        }

        // Delete FD function
        function deleteFD(fdId) {
            if (confirm('Are you sure you want to delete ' + fdId + '?')) {
                alert('Deleted: ' + fdId);
                // Call delete servlet or API
                // Then reload the page or remove the row
            }
        }

        // Auto-open Fixed Deposits dropdown on FDList page
        document.addEventListener('DOMContentLoaded', function() {
            const fdDropdown = document.getElementById('fdDropdown');
            const fdNavItem = document.getElementById('fdNavItem');
            
            if (fdDropdown && fdNavItem) {
                fdDropdown.classList.add('show');
                fdNavItem.classList.add('open');
            }
            
         // Check for success message
            const fdSuccess = sessionStorage.getItem('fdSuccess');

            if (fdSuccess === 'true') {
                const successMsg = document.getElementById('successMessage');

                if (successMsg) {
                    // Show
                    successMsg.classList.add('show');
                    sessionStorage.removeItem('fdSuccess');

                    // Hide after 3 seconds
                    setTimeout(() => {
                        successMsg.classList.remove('show');
                        successMsg.classList.add('hide');

                        // Clean up after animation ends
                        successMsg.addEventListener('animationend', () => {
                            successMsg.classList.remove('hide');
                        }, { once: true });

                    }, 3000);
                }
            }
        });
        
        
    </script>
    <script src="fdData.js"></script>
    
    <!-- Success Message -->
    <div class="success-message" id="successMessage">
        New Fixed Deposit added successfully!
    </div>
</body>
</html>
