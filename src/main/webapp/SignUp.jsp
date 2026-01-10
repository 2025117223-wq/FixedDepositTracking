<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up - Fixed Deposit Tracking System</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Libre+Baskerville:wght@400;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f5f5;
            min-height: 100vh;
            display: flex;
        }

        .container {
            display: flex;
            width: 100%;
            min-height: 100vh;
        }

        .left-panel {
            flex: 0 0 30%;
            background: linear-gradient(180deg, #1e5765 0%, #2d7080 100%);
            display: flex;
            flex-direction: column;
            justify-content: flex-start;
            align-items: flex-start;
            padding: 10px 40px 60px 40px;
            color: white;
            position: relative;
            background-image: 
                linear-gradient(rgba(12, 55, 63, 0.7), rgba(12, 55, 63, 0.8)),
                url('images/signupBG.png');
            background-size: cover;
            background-position: center;
        }

        .logo {
            width: 240px;
            height: 240px;
            position: relative;
            margin-bottom: 0px;
        }

        .logo img {
            width: 100%;
            height: 100%;
            object-fit: contain;
        }

        .system-title {
            text-align: left;
            margin-left: 20px;
            
        }

        .system-title h1 {
            font-size: 4rem;
            font-weight: 700;
            color: #ff9a5a;
            line-height: 1.1;
            text-shadow: 3px 3px 6px rgba(0,0,0,0.2);
            font-family: 'Libre Baskerville', serif;
        }

        .right-panel {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 60px;
            background: white;
            position: relative;
            overflow: hidden;
        }

        .right-panel::before {
            content: '';
            position: absolute;
            top: -150px;
            right: 150px;
            width: 1000px;
            height: 2000px;
            background-image: url('images/line.png');
            background-size: contain;
            background-repeat: no-repeat;
            opacity: 1;
            pointer-events: none;
        }

        .signup-form {
            width: 100%;
            max-width: 900px;
            position: relative;
            z-index: 1;
        }

        .form-header {
            text-align: center;
            margin-bottom: 50px;
        }

        .form-header h2 {
            font-size: 3rem;
            color: #1a4d5e;
            font-weight: 700;
            margin-bottom: 15px;
        }

        .form-header .divider {
            width: 100%;
            height: 4px;
            background: #1a4d5e;
            border: none;
            border-top: 3px dashed #1a4d5e;
            background: transparent;
        }

        .form-row {
            display: flex;
            gap: 30px;
            margin-bottom: 30px;
        }

        .form-group {
            flex: 1;
        }

        .form-group.full-width {
            width: 100%;
        }

        .input-wrapper {
            position: relative;
            display: flex;
            align-items: center;
        }

        .input-icon {
            position: absolute;
            left: 25px;
            width: 40px;
            height: 40px;
            margin-left:-10px;
            z-index: 1;
            object-fit: contain;
        }
        
        .phone-icon {
            position: absolute;
            left: 25px;
            width: 56px;
            height: 56px;
            margin-left:-17px;
            z-index: 1;
            object-fit: contain;
        }
        
        .password-icon {
            position: absolute;
            left: 25px;
            width: 35px;
            height: 35px;
            margin-left:-5px;
            z-index: 1;
            object-fit: contain;
        }
        
        .address-icon {
            position: absolute;
            left: 25px;
            width: 39px;
            height: 39px;
            margin-left:-8px;
            z-index: 1;
            object-fit: contain;
        }

        input[type="text"],
        input[type="email"],
        input[type="tel"],
        input[type="password"] {
            width: 100%;
            padding: 20px 25px 20px 65px;
            border: 2px solid #d0d0d0;
            border-radius: 50px;
            font-size: 16px;
            transition: all 0.3s ease;
            background: #fafafa;
            color: #333;
        }

        input:focus {
            outline: none;
            border-color: #1a4d5e;
            background: white;
        }

        input::placeholder {
            color: #a8a8a8;
        }

        .profile-upload {
            flex: 1;
        }

        .profile-upload-wrapper {
            position: relative;
            display: flex;
            align-items: center;
        }

        .profile-upload-input {
            width: 100%;
            padding: 20px 25px 20px 65px;
            border: 2px solid #d0d0d0;
            border-radius: 50px;
            font-size: 16px;
            transition: all 0.3s ease;
            background: #fafafa;
            color: #a8a8a8;
            cursor: pointer;
            display: flex;
            align-items: center;
        }

        .profile-upload-input:hover {
            border-color: #1a4d5e;
            background: white;
        }

        .profile-icon {
            position: absolute;
            left: 25px;
            width: 40px;
            height: 40px;
            margin-left: -10px;
            z-index: 1;
            object-fit: contain;
            pointer-events: none;
        }

        .file-format {
            color: #7f8c8d;
            font-size: 13px;
            margin-top: 8px;
            margin-left: 25px;
        }

        input[type="file"] {
            display: none;
        }

        .file-format {
            color: #7f8c8d;
            font-size: 14px;
            margin-top: 1px;
            padding-left: 42px;
        }

        .file-preview {
            margin-top: 15px;
            color: #27ae60;
            font-size: 14px;
            font-weight: 500;
            padding-left: 25px;
        }

        .submit-btn {
            width: 70%;
            max-width: 400px;
            padding: 20px;
            background: linear-gradient(135deg, #ff9a5a, #ff8547);
            border: none;
            border-radius: 50px;
            color: white;
            font-size: 20px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            margin: 50px auto 0;
            display: block;
            box-shadow: 0 6px 20px rgba(255, 154, 90, 0.3);
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(255, 154, 90, 0.4);
        }

        .login-link {
            text-align: center;
            margin-top: 30px;
            color: #34495e;
            font-size: 16px;
        }

        .login-link a {
            color: #1a4d5e;
            font-weight: 700;
            text-decoration: underline;
        }

        .error-message {
            color: #e74c3c;
            font-size: 13px;
            margin-top: 5px;
            margin-left: 25px;
            display: none;
        }

        .error-message.show {
            display: block;
        }

        input.error {
            border-color: #e74c3c;
        }

        /* Confirmation Modal */
        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        .modal-overlay.show {
            display: flex;
        }

        .modal-content {
            background: white;
            padding: 40px 50px;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
            text-align: center;
            max-width: 700px;
            width: 90%;
            font-family: 'Inter', sans-serif;
        }

        .modal-content h3 {
            font-size: 1.2rem;
            color: #2c3e50;
            margin-bottom: 30px;
            font-weight: 600;
            font-family: 'Inter', sans-serif;
        }

        .modal-buttons {
            display: flex;
            gap: 20px;
            justify-content: center;
        }

        .modal-btn {
            padding: 12px 40px;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            border: none;
            font-family: 'Inter', sans-serif;
        }

        .modal-btn-no {
            background: white;
            color: #2c3e50;
            border: 2px solid #d0d0d0;
        }

        .modal-btn-no:hover {
            background: #f5f5f5;
        }

        .modal-btn-yes {
            background: #1a4d5e;
            color: white;
        }

        .modal-btn-yes:hover {
            background: #154050;
        }

        @media (max-width: 1200px) {
            .system-title h1 {
                font-size: 3.5rem;
            }
            
            .form-header h2 {
                font-size: 2.5rem;
            }
        }

        @media (max-width: 968px) {
            .container {
                flex-direction: column;
            }

            .left-panel {
                flex: 0 0 auto;
                padding: 40px 20px;
            }

            .logo {
                width: 100px;
                height: 100px;
            }

            .system-title h1 {
                font-size: 2.5rem;
            }

            .right-panel {
                padding: 40px 20px;
            }

            .form-row {
                flex-direction: column;
                gap: 25px;
            }

            .profile-upload {
                margin-top: 30px;
            }

            .submit-btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="left-panel">
            <div class="logo">
                <img src="images/Logo.png" alt="Infra Desa Johor Logo">
            </div>
            <div class="system-title">
                <h1>Fixed<br>Deposit<br>Tracking<br>System</h1>
            </div>
        </div>

        <div class="right-panel">
            <div class="signup-form">
                <div class="form-header">
                    <h2>Sign Up Account</h2>
                    <hr class="divider">
                </div>

                <form id="signupForm" action="SignUpServlet" method="POST" enctype="multipart/form-data">
                    <div class="form-row">
                        <div class="form-group">
                            <div class="input-wrapper">
                                <img src="images/icons/user.jpg" alt="User" class="input-icon">
                                <input type="text" id="fullName" name="fullName" placeholder="Full Name" required>
                            </div>
                            <div class="error-message" id="fullNameError">Please enter your full name</div>
                        </div>
                        <div class="form-group">
                            <div class="input-wrapper">
                                <img src="images/icons/phone.png" alt="Phone" class="phone-icon">
                                <input type="tel" id="phoneNumber" name="phoneNumber" placeholder="Phone Number" required pattern="[0-9]+">
                            </div>
                            <div class="error-message" id="phoneError">Please enter a valid phone number (numbers only)</div>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <div class="input-wrapper">
                                <img src="images/icons/email.png" alt="Email" class="input-icon">
                                <input type="email" id="email" name="email" placeholder="Email Address" required>
                            </div>
                            <div class="error-message" id="emailError">Please enter a valid email address</div>
                        </div>
                        <div class="form-row">
                        <div class="form-group">
                            <div class="input-wrapper">
                                <label for="staffRole" class="staff-role-label">Staff Role</label>
                                <select id="staffRole" name="staffRole" required>
                                    <option value="" disabled selected>Choose Role</option>
                                    <option value="Adminstration">Administration</option>
                                    <option value="FinanceExecutive">Finance Executive</option>
                                    <option value="FinanceStaff">Finance Staff</option>
                                </select>
                            </div>
                            <div class="error-message" id="staffRoleError">Please select a staff role</div>
                        </div>
                    </div>
                        <div class="form-group">
                            <div class="input-wrapper">
                                <img src="images/icons/password.png" alt="Password" class="password-icon">
                                <input type="password" id="password" name="password" placeholder="Password" required minlength="8">
                            </div>
                            <div class="error-message" id="passwordError">Password must be at least 8 characters</div>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <div class="input-wrapper">
                                <img src="images/icons/haddress.png" alt="Home" class="address-icon">
                                <input type="text" id="homeAddress" name="homeAddress" placeholder="Home Address" required>
                            </div>
                            <div class="error-message" id="addressError">Please enter your home address</div>
                        </div>
                        <div class="form-group profile-upload">
                            <label for="profilePicture" class="profile-upload-wrapper">
                                <img src="images/icons/file.png" alt="Upload" class="profile-icon">
                                <div class="profile-upload-input" id="uploadLabel">Upload Profile Picture</div>
                            </label>
                            <div class="file-format">JPEG, PNG</div>
                            <input type="file" id="profilePicture" name="profilePicture" accept=".jpg,.jpeg,.png" required>
                            <div class="error-message" id="fileError">Please select a profile picture</div>
                        </div>
                    </div>

                    <button type="submit" class="submit-btn">Sign Up</button>

                    <div class="login-link">
                        Already have an account? <a href="Login.jsp">Login Now</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Confirmation Modal -->
    <div class="modal-overlay" id="confirmModal">
        <div class="modal-content">
            <h3>Are you sure want to submit this information?</h3>
            <div class="modal-buttons">
                <button type="button" class="modal-btn modal-btn-no" id="modalNo">No</button>
                <button type="button" class="modal-btn modal-btn-yes" id="modalYes">Yes</button>
            </div>
        </div>
    </div>

    <script>
        // Form validation
        const form = document.getElementById('signupForm');
        const fullName = document.getElementById('fullName');
        const phoneNumber = document.getElementById('phoneNumber');
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const homeAddress = document.getElementById('homeAddress');
        const profilePicture = document.getElementById('profilePicture');

        // Phone number validation - only numbers
        phoneNumber.addEventListener('input', function(e) {
            this.value = this.value.replace(/[^0-9]/g, '');
        });

        // File upload preview
        profilePicture.addEventListener('change', function(e) {
            const file = e.target.files[0];
            const uploadLabel = document.getElementById('uploadLabel');
            const fileError = document.getElementById('fileError');
            
            if (file) {
                const validTypes = ['image/jpeg', 'image/jpg', 'image/png'];
                if (!validTypes.includes(file.type)) {
                    fileError.textContent = 'Please select a valid image file (JPEG or PNG)';
                    fileError.classList.add('show');
                    this.value = '';
                    uploadLabel.textContent = 'Upload Profile Picture';
                    uploadLabel.style.color = '#a8a8a8';
                    return;
                }
                
                if (file.size > 5 * 1024 * 1024) { // 5MB limit
                    fileError.textContent = 'File size must be less than 5MB';
                    fileError.classList.add('show');
                    this.value = '';
                    uploadLabel.textContent = 'Upload Profile Picture';
                    uploadLabel.style.color = '#a8a8a8';
                    return;
                }
                
                uploadLabel.textContent = file.name;
                uploadLabel.style.color = '#333';
                fileError.classList.remove('show');
                this.classList.remove('error');
            }
        });

        // Form submission validation
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            let isValid = true;

            // Reset all errors
            document.querySelectorAll('.error-message').forEach(el => el.classList.remove('show'));
            document.querySelectorAll('input').forEach(el => el.classList.remove('error'));

            // Full Name validation
            if (fullName.value.trim() === '') {
                showError('fullName', 'fullNameError', 'Please enter your full name');
                isValid = false;
            }

            // Phone Number validation
            if (phoneNumber.value.trim() === '' || !/^[0-9]+$/.test(phoneNumber.value)) {
                showError('phoneNumber', 'phoneError', 'Please enter a valid phone number (numbers only)');
                isValid = false;
            }

            // Email validation
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(email.value)) {
                showError('email', 'emailError', 'Please enter a valid email address');
                isValid = false;
            }

            // Password validation
            if (password.value.length < 8) {
                showError('password', 'passwordError', 'Password must be at least 8 characters');
                isValid = false;
            }

            // Home Address validation
            if (homeAddress.value.trim() === '') {
                showError('homeAddress', 'addressError', 'Please enter your home address');
                isValid = false;
            }

            // Profile Picture validation
            if (!profilePicture.files || profilePicture.files.length === 0) {
                document.getElementById('fileError').classList.add('show');
                isValid = false;
            }

            if (isValid) {
                // Show confirmation modal
                document.getElementById('confirmModal').classList.add('show');
            }
        });

        // Modal buttons
        document.getElementById('modalNo').addEventListener('click', function() {
            document.getElementById('confirmModal').classList.remove('show');
        });

        document.getElementById('modalYes').addEventListener('click', function() {
            document.getElementById('confirmModal').classList.remove('show');
            // For demo purposes, redirect to login with success message
            // In production, uncomment the line below to submit to servlet
            // form.submit();
            
            // Demo: Show success on login page
            window.location.href = 'Login.jsp?signup=success';
        });

        // Close modal when clicking outside
        document.getElementById('confirmModal').addEventListener('click', function(e) {
            if (e.target === this) {
                this.classList.remove('show');
            }
        });

        function showError(inputId, errorId, message) {
            document.getElementById(inputId).classList.add('error');
            const errorEl = document.getElementById(errorId);
            errorEl.textContent = message;
            errorEl.classList.add('show');
        }
    </script>
</body>
</html>