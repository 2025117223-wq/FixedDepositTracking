<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Bank - Fixed Deposit Tracking System</title>
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
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .form-container {
            background: white;
            border-radius: 20px;
            padding: 50px 60px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            max-width: 500px;
            width: 100%;
        }

        .form-title {
            font-size: 28px;
            font-weight: 600;
            color: #2c3e50;
            text-align: center;
            margin-bottom: 40px;
        }

        .form-group {
            margin-bottom: 30px;
        }

        .form-group label {
            display: block;
            font-size: 15px;
            font-weight: 500;
            color: #2c3e50;
            margin-bottom: 10px;
        }

        .form-group input {
            width: 100%;
            padding: 15px 18px;
            border: 2px solid #d0d0d0;
            border-radius: 8px;
            font-size: 15px;
            font-family: 'Inter', sans-serif;
            transition: all 0.3s ease;
        }

        .form-group input:focus {
            outline: none;
            border-color: #003f5c;
        }

        .form-group input::placeholder {
            color: #a0a0a0;
        }

        .btn-container {
            display: flex;
            justify-content: center;
            margin-top: 40px;
        }

        .btn-submit {
            padding: 15px 80px;
            background: #003f5c;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            font-family: 'Inter', sans-serif;
        }

        .btn-submit:hover {
            background: #002d42;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 63, 92, 0.3);
        }

        .error-message {
            color: #e74c3c;
            font-size: 13px;
            margin-top: 8px;
            display: none;
        }

        @media (max-width: 768px) {
            .main-content {
                margin-left: 200px;
            }

            .form-container {
                padding: 40px 30px;
            }

            .page-content {
                padding: 20px;
            }
        }
    </style>
</head>
<body>
    <%@ include file="includes/sidebar.jsp" %>

    <div class="main-content">
        <div class="header">
            <h1>Create New Bank</h1>
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
            <div class="form-container">
                <h2 class="form-title">Create Bank</h2>

                <form id="createBankForm" onsubmit="submitBank(event)">
                    <div class="form-group">
                        <label for="bankName">Bank Name</label>
                        <input 
                            type="text" 
                            id="bankName" 
                            name="bankName" 
                            placeholder="Enter bank name"
                            required
                        >
                        <div class="error-message" id="errorMessage">Bank name is required</div>
                    </div>

                    <div class="btn-container">
                        <button type="submit" class="btn-submit">Submit</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Auto-expand Bank dropdown in sidebar
        document.addEventListener('DOMContentLoaded', function() {
            const bankDropdown = document.getElementById('bankDropdown');
            const bankNavItem = document.getElementById('bankNavItem');
            
            if (bankDropdown && bankNavItem) {
                bankDropdown.classList.add('show');
                bankNavItem.classList.add('open');
            }
        });

        function submitBank(event) {
            event.preventDefault();

            const bankName = document.getElementById('bankName').value.trim();
            const errorMessage = document.getElementById('errorMessage');

            // Validation
            if (!bankName) {
                errorMessage.style.display = 'block';
                return;
            } else {
                errorMessage.style.display = 'none';
            }

            // In real implementation, this would submit to servlet/API
            console.log('Creating bank:', bankName);

            // Clear form
            document.getElementById('createBankForm').reset();

            // Redirect immediately to Bank Lists page with success parameter
            window.location.href = 'BankList.jsp?success=created';
        }

        // Real-time validation
        document.getElementById('bankName').addEventListener('input', function() {
            const errorMessage = document.getElementById('errorMessage');
            if (this.value.trim()) {
                errorMessage.style.display = 'none';
            }
        });
    </script>
</body>
</html>
