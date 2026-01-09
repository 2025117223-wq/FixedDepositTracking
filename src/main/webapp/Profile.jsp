<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile - Fixed Deposit Tracking System</title>
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
            max-height: 100vh;
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

        .user-avatar-small {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #d0d0d0;
        }

        .user-avatar-small img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            object-fit: cover;
        }

        .page-content {
            padding: 40px;
            flex: 1;
            display: flex;
            gap: 1px;
            margin-top: -18px;
        }

        .profile-left {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-left: 60px;
            margin-top: 140px;
            gap: 10px;
        }

        .profile-avatar {
            width: 180px;
            height: 180px;
            border-radius: 50%;
            background: #d0d0d0;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
        }

        .profile-avatar img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .profile-avatar-icon {
            font-size: 80px;
            color: #7f8c8d;
        }

        .profile-greeting {
            font-size: 24px;
            font-weight: 600;
            color: #2c3e50;
            text-align: center;
        }

        .profile-right {
            flex: 1;
        }

        .info-card {
            background: white;
            border-radius: 20px;
            padding: 40px 50px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            border: 2px solid #e0e0e0;
            margin-left: 90px;
        }

        .info-title {
            font-size: 28px;
            font-weight: 600;
            color: #ff9747;
            margin-bottom: 30px;
            text-align: center;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 25px;
        }

        .info-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .info-label {
            font-size: 14px;
            font-weight: 500;
            color: #2c3e50;
        }

        .info-value {
            width: 100%;
            padding: 12px 18px;
            background: #e8e8e8;
            border-radius: 8px;
            font-size: 15px;
            color: #2c3e50;
            border: none;
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        .info-value:focus {
            outline: none;
            background: #d8d8d8;
        }

        .info-group.full-width {
            grid-column: 1 / -1;
        }

        .file-upload-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
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
            border-color: #003f5c;
            background: #f8f9fa;
        }

        .file-icon {
            font-size: 24px;
        }

        .file-info {
            font-size: 13px;
            color: #7f8c8d;
        }

        .file-input {
            display: none;
        }

        .btn-container {
            display: flex;
            justify-content: flex-end;
            margin-top: 30px;
        }

        .btn-update {
            padding: 15px 50px;
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

        .btn-update:hover {
            background: #002d42;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 63, 92, 0.3);
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

        @media (max-width: 1024px) {
            .page-content {
                flex-direction: column;
            }

            .info-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
    <%@ include file="includes/sidebar.jsp" %>

    <div class="main-content">
        <div class="header">
            <h1>Dashboard</h1>
            <div class="user-profile">
                <div class="user-info">
                    <div class="user-name">Nor Azlina</div>
                    <div class="user-role">Administrator</div>
                </div>
                <div class="user-avatar-small">
                    <img src="images/icons/user.jpg" alt="User Avatar" onerror="this.style.display='none'">
                </div>
            </div>
        </div>

        <div class="page-content">
            <div class="profile-left">
                <div class="profile-avatar" id="profileAvatar">
                    <span class="profile-avatar-icon">👤</span>
                </div>
                <div class="profile-greeting">Hi, Nor Azlina !</div>
            </div>

            <div class="profile-right">
                <div class="info-card">
                    <h2 class="info-title">Your Information Details</h2>

                    <form id="profileForm" onsubmit="updateProfile(event)">
                        <div class="info-grid">
                            <div class="info-group">
                                <label class="info-label">Name</label>
                                <input type="text" class="info-value" id="name" value="Nor Azlina" required>
                            </div>

                            <div class="info-group">
                                <label class="info-label">Staff ID</label>
                                <input type="text" class="info-value" id="staffId" value="Admin01" readonly style="cursor: not-allowed;">
                            </div>

                            <div class="info-group">
                                <label class="info-label">Email</label>
                                <input type="email" class="info-value" id="email" value="norazlina@gmail.com" required>
                            </div>

                            <div class="info-group">
                                <label class="info-label">Number Phone</label>
                                <input type="tel" class="info-value" id="phone" value="0123456789" required>
                            </div>

                            <div class="info-group">
                                <label class="info-label">Password</label>
                                <input type="password" class="info-value" id="password" value="****************" required>
                            </div>

                            <div class="info-group">
                                <label class="info-label">Profile Picture</label>
                                <div class="file-upload-group">
                                    <label for="profilePicture" class="file-upload-btn">
                                        <span class="file-icon">📁</span>
                                        <span>Choose File</span>
                                    </label>
                                    <input type="file" id="profilePicture" class="file-input" accept=".jpg,.jpeg,.png" onchange="handleFileSelect(event)">
                                    <span class="file-info">JPEG, PNG.</span>
                                </div>
                            </div>

                            <div class="info-group full-width">
                                <label class="info-label">Address</label>
                                <input type="text" class="info-value" id="address" value="No 1, Jalan Ringgit Malaysia, Taman Tropika 9, 12345, Ipoh, Perak" required>
                            </div>
                        </div>

                        <div class="btn-container">
                            <button type="submit" class="btn-update">Update Profile</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Success Message -->
    <div class="success-message" id="successMessage"></div>

    <!-- Update Confirmation Modal -->
    <div class="modal-overlay" id="updateModal">
        <div class="modal-content">
            <div class="modal-message">
                Are you sure you want to update your profile information?
            </div>
            <div class="modal-buttons">
                <button class="modal-btn modal-btn-no" onclick="closeUpdateModal()">No</button>
                <button class="modal-btn modal-btn-yes" onclick="confirmUpdate()">Yes</button>
            </div>
        </div>
    </div>

    <script>
        let profileData = {};

        function updateProfile(event) {
            event.preventDefault();

            // Store form data
            profileData = {
                name: document.getElementById('name').value,
                email: document.getElementById('email').value,
                phone: document.getElementById('phone').value,
                password: document.getElementById('password').value,
                address: document.getElementById('address').value
            };

            // Show confirmation modal
            document.getElementById('updateModal').classList.add('active');
        }

        function closeUpdateModal() {
            document.getElementById('updateModal').classList.remove('active');
        }

        function confirmUpdate() {
            closeUpdateModal();

            // In real implementation, this would submit to servlet/API
            console.log('Updating profile:', profileData);

            // Show success message
            showSuccessMessage('Profile updated successfully!');
        }

        function handleFileSelect(event) {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const avatar = document.getElementById('profileAvatar');
                    avatar.innerHTML = '<img src="' + e.target.result + '" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">';
                };
                reader.readAsDataURL(file);
            }
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
