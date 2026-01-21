<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fd.dao.StaffDAO" %>
<%@ page import="com.fd.model.Staff" %>
<%@ page import="java.util.List" %>

<%
    // Load existing staff for manager dropdown
    StaffDAO staffDAO = new StaffDAO();
    List<Staff> allStaff = staffDAO.getAllStaff();
%>
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
            border: none;
            border-top: 3px dashed #1a4d5e;
            background: transparent;
        }

        .form-row {
            display: flex;
            gap: 30px;
            margin-bottom: 30px;
        }

        .form-row .form-group {
            flex: 1;
            min-width: 0;
        }

        .form-row.fullwidth .form-group {
            flex: 1 1 100%;
        }

        .input-wrapper {
            position: relative;
            display: flex;
            align-items: center;
        }

        .phone-icon {
            position: absolute;
            left: 15px;
            object-fit: contain;
            z-index: 2;
        }

        .input-icon,
        .password-icon,
        .address-icon,
        .manager-icon,
        .profile-icon {
            position: absolute;
            left: 25px;
            object-fit: contain;
            z-index: 2;
        }

        .role-icon {
            position: absolute;
            left: 12px;
            object-fit: contain;
            z-index: 2;
        }

        .input-icon { width: 40px; height: 40px; }
        .phone-icon { width: 56px; height: 56px; }
        .password-icon { width: 35px; height: 35px; }
        .address-icon { width: 37px; height: 37px; }
        .profile-icon { width: 40px; height: 40px; }
        .role-icon { width: 68px; height: 68px; }
        .manager-icon { width: 39px; height: 39px; }

        input[type="text"],
        input[type="email"],
        input[type="tel"],
        input[type="password"],
        select {
            width: 100%;
            padding: 20px 25px 20px 90px;
            border: 2px solid #d0d0d0;
            border-radius: 50px;
            font-size: 16px;
            background: #fafafa;
            color: #333;
            transition: border-color 0.3s ease, background 0.3s ease;
        }

        input::placeholder {
            color: #a8a8a8;
        }

        input:focus,
        select:focus {
            border-color: #1a4d5e;
            background: #fff;
            outline: none;
        }

        select {
            appearance: none;
            -webkit-appearance: none;
            -moz-appearance: none;
            color: #a8a8a8;
        }

        select:valid {
            color: #333;
        }

        /* Manager select - show as invalid when not selected */
        select#managerId:invalid {
            color: #a8a8a8;
        }

        .profile-upload-wrapper {
            position: relative;
            display: flex;
            align-items: center;
            width: 100%;
            cursor: pointer;
        }

        .profile-upload-input {
            width: 100%;
            padding: 20px 25px 20px 90px;
            border: 2px solid #d0d0d0;
            border-radius: 50px;
            background: #fafafa;
            cursor: pointer;
            color: #a8a8a8;
            transition: border-color 0.3s ease, background 0.3s ease;
            min-height: 62px;
            display: flex;
            align-items: center;
        }

        .profile-upload-input:hover {
            border-color: #1a4d5e;
            background: #fff;
        }

        .profile-upload-wrapper.error .profile-upload-input {
            border-color: #e74c3c;
        }

        input[type="file"] {
            display: none;
        }

        .file-format {
            font-size: 14px;
            color: #7f8c8d;
            margin-left: 42px;
            margin-top: 4px;
        }

        .server-error {
            background: rgba(231, 76, 60, 0.12);
            border: 1px solid rgba(231, 76, 60, 0.65);
            color: #e74c3c;
            padding: 10px 12px;
            border-radius: 10px;
        }

        .submit-btn {
            width: 70%;
            max-width: 400px;
            padding: 20px;
            margin: 50px auto 0;
            display: block;
            border-radius: 50px;
            border: none;
            font-size: 20px;
            font-weight: 600;
            background: linear-gradient(135deg, #ff9a5a, #ff8547);
            color: #fff;
            cursor: pointer;
            transition: transform 0.2s ease;
        }

        .submit-btn:hover {
            transform: translateY(-2px);
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
            font-size: 13px;
            color: #e74c3c;
            margin-top: 6px;
            margin-left: 25px;
            display: none;
        }

        .error-message.show {
            display: block;
        }

        input.error,
        select.error {
            border-color: #e74c3c;
        }

        .modal-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.55);
            z-index: 9999;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .modal-overlay.show {
            display: flex;
        }

        .modal-content {
            background: #fff;
            width: 100%;
            max-width: 520px;
            padding: 28px 30px;
            border-radius: 18px;
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
            text-align: center;
        }

        .modal-content h3 {
            margin: 0 0 18px 0;
            font-size: 18px;
            font-weight: 700;
            color: #000;
        }

        .modal-buttons {
            display: flex;
            gap: 16px;
            justify-content: center;
        }

        .modal-btn {
            min-width: 120px;
            padding: 12px 22px;
            border-radius: 12px;
            border: 2px solid #d0d0d0;
            font-weight: 700;
            cursor: pointer;
            background: #fff;
        }

        .modal-btn-no {
            background: #fff;
            color: #000;
        }

        .modal-btn-yes {
            background: #1a4d5e;
            border-color: #1a4d5e;
            color: #fff;
        }

        @media (max-width: 968px) {
            .container {
                flex-direction: column;
            }

            .left-panel {
                flex: 0 0 auto;
            }

            .form-row {
                flex-direction: column;
            }

            .submit-btn {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<div class="container">

    <!-- LEFT PANEL -->
    <div class="left-panel">
        <div class="logo">
            <img src="images/Logo.png" alt="Infra Desa Johor Logo">
        </div>
        <div class="system-title">
            <h1>Fixed<br>Deposit<br>Tracking<br>System</h1>
        </div>
    </div>

    <!-- RIGHT PANEL -->
    <div class="right-panel">
        <div class="signup-form">

            <div class="form-header">
                <h2>Sign Up Account</h2>
                <hr class="divider">
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message show server-error" style="margin-bottom: 12px;">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <!-- FORM SUBMITS TO SignUpServlet -->
            <form id="signupForm" action="SignUpServlet" method="POST" enctype="multipart/form-data" novalidate>

                <!-- Full Name (Full Width) -->
                <div class="form-row fullwidth">
                    <div class="form-group">
                        <div class="input-wrapper">
                            <img src="images/icons/user.jpg" alt="User" class="input-icon">
                            <input type="text" id="fullName" name="name" placeholder="Full Name">
                        </div>
                        <div class="error-message" id="fullNameError">Please enter your full name</div>
                    </div>
                </div>

                <!-- Row 1: Phone | Email -->
                <div class="form-row">
                    <div class="form-group">
                        <div class="input-wrapper">
                            <img src="images/icons/phone.png" alt="Phone" class="phone-icon">
                            <input type="tel" id="phoneNumber" name="phone" placeholder="Phone Number" pattern="[0-9]+">
                        </div>
                        <div class="error-message" id="phoneError">Please enter a valid phone number (numbers only)</div>
                    </div>

                    <div class="form-group">
                        <div class="input-wrapper">
                            <img src="images/icons/email.png" alt="Email" class="input-icon">
                            <input type="email" id="email" name="email" placeholder="Email Address">
                        </div>
                        <div class="error-message" id="emailError">Please enter a valid email address</div>
                    </div>
                </div>

                <!-- Row 2: Address | Profile Picture -->
                <div class="form-row">
                    <div class="form-group fullwidth">
                        <div class="input-wrapper">
                            <img src="images/icons/haddress.png" alt="Home" class="address-icon">
                            <input type="text" id="homeAddress" name="address" placeholder="Home Address">
                        </div>
                        <div class="error-message" id="addressError">Please enter your home address</div>
                    </div>

                    <div class="form-group profile-upload">
                        <label for="profilePicture" class="profile-upload-wrapper" id="uploadWrapper">
                            <img src="images/icons/file.png" alt="Upload" class="profile-icon">
                            <div class="profile-upload-input" id="uploadLabel">Upload Profile Picture</div>
                        </label>

                        <div class="file-format">JPEG, PNG</div>
                        <input type="file" id="profilePicture" name="profilePicture" accept=".jpg,.jpeg,.png">
                        <div class="error-message" id="fileError">Please upload your profile picture</div>
                    </div>
                </div>

                <!-- Row 3: Role | Manager -->
                <div class="form-row">
                    <div class="form-group">
                        <div class="input-wrapper select-wrapper">
                            <img src="images/icons/role.png" alt="Home" class="address-icon">
                            <select id="staffRole" name="role">
                                <option value="" disabled selected>Select Role</option>
                                <option value="Senior Finance Manager">Senior Finance Manager</option>
                                <option value="Finance Executive">Finance Executive</option>
                            </select>
                        </div>
                        <div class="error-message" id="roleError">Please select a role</div>
                    </div>

                    <div class="form-group">
                        <div class="input-wrapper select-wrapper">
                            <img src="images/icons/role.png" alt="Home" class="address-icon">
                            <!-- REMOVED required attribute -->
                            <select id="managerId" name="managerId">
                                <option value="" disabled selected>Select Manager</option>
                                <option value="0">I'm Manager</option>
                                <% 
								    if (allStaff != null) {
								        for (Staff mgr : allStaff) {
								            // Skip inactive users
								            if (!"Active".equalsIgnoreCase(mgr.getStatus())) {
								                continue;
								            }
								            
								            boolean isEligible = false;
								            String role = mgr.getRole() != null ? mgr.getRole() : "";
								            
								            // Show ONLY Senior Finance Manager with no manager
								            if (role.equals("Senior Finance Manager") &&
								                (mgr.getManagerId() == 0 || mgr.getManagerId() == -1)) {
								                isEligible = true;
								            }
								            
								            if (isEligible) {
								%>
								    <option value="<%= mgr.getStaffId() %>">
								        <%= mgr.getName() %> - <%= mgr.getRole() %>
								    </option>
								<% 
								            }
								        }
								    }
								%>
                            </select>
                        </div>
                        <!-- NEW: Error message for manager select -->
                        <div class="error-message" id="managerError">Please select a manager or choose "No Manager"</div>
                    </div>
                </div>

                <!-- Row 4: Password | Re-confirm Password -->
                <div class="form-row">
                    <div class="form-group">
                        <div class="input-wrapper" style="position: relative;">
                            <img src="images/icons/password.png" alt="Password" class="password-icon">
                            <input type="password" id="password" name="password" placeholder="Password" minlength="6">
 
                            <button type="button" id="togglePassword"
                                    style="position:absolute; right:25px; background:none; border:none; cursor:pointer; z-index:2;">
                                <img id="passwordIcon" src="images/icons/hidepass.png" alt="Show Password" style="width:25px; height:25px;">
                            </button>
                        </div>
                        <div class="error-message" id="passwordError">Password must be at least 6 characters</div>
                    </div>

                    <div class="form-group">
                        <div class="input-wrapper" style="position: relative;">
                            <img src="images/icons/password.png" alt="Confirm Password" class="password-icon">
                            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Re-confirm Password" minlength="6">

                            <button type="button" id="toggleConfirmPassword"
                                    style="position:absolute; right:25px; background:none; border:none; cursor:pointer; z-index:2;">
                                <img id="confirmPasswordIcon" src="images/icons/hidepass.png" alt="Show Password" style="width:25px; height:25px;">
                            </button>
                        </div>
                        <div class="error-message" id="confirmPasswordError">Passwords do not match</div>
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
        <h3>Are you sure you want to submit this information?</h3>
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
    const confirmPassword = document.getElementById('confirmPassword');
    const homeAddress = document.getElementById('homeAddress');
    const profilePicture = document.getElementById('profilePicture');
    const uploadWrapper = document.getElementById('uploadWrapper');
    const uploadLabel = document.getElementById('uploadLabel');
    const fileError = document.getElementById('fileError');
    const staffRole = document.getElementById('staffRole');
    const managerId = document.getElementById('managerId');

    // Password toggle elements
    const togglePasswordButton = document.getElementById('togglePassword');
    const passwordIcon = document.getElementById('passwordIcon');
    const toggleConfirmPasswordButton = document.getElementById('toggleConfirmPassword');
    const confirmPasswordIcon = document.getElementById('confirmPasswordIcon');

    // Toggle password visibility
    togglePasswordButton.addEventListener('click', () => {
        if (password.type === 'password') {
            password.type = 'text';
            passwordIcon.src = 'images/icons/eyeEnable.png';
            passwordIcon.alt = 'Show Password';
        } else {
            password.type = 'password';
            passwordIcon.src = 'images/icons/eyeDisable.png';
            passwordIcon.alt = 'Hide Password';
        }
    });

    // Toggle confirm password visibility
    toggleConfirmPasswordButton.addEventListener('click', () => {
        if (confirmPassword.type === 'password') {
            confirmPassword.type = 'text';
            confirmPasswordIcon.src = 'images/icons/eyeEnable.png';
            confirmPasswordIcon.alt = 'Show Password';
        } else {
            confirmPassword.type = 'password';
            confirmPasswordIcon.src = 'images/icons/eyeDisable.png';
            confirmPasswordIcon.alt = 'Hide Password';
        }
    });

    // Phone number validation - only numbers
    phoneNumber.addEventListener('input', function(e) {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    // File upload preview
    profilePicture.addEventListener('change', function(e) {
        const file = e.target.files[0];
        
        if (file) {
            const validTypes = ['image/jpeg', 'image/jpg', 'image/png'];
            if (!validTypes.includes(file.type)) {
                fileError.textContent = 'Please select a valid image file (JPEG or PNG)';
                fileError.classList.add('show');
                uploadWrapper.classList.add('error');
                this.value = '';
                uploadLabel.textContent = 'Upload Profile Picture';
                uploadLabel.style.color = '#a8a8a8';
                return;
            }
            
            if (file.size > 5 * 1024 * 1024) { // 5MB limit
                fileError.textContent = 'File size must be less than 5MB';
                fileError.classList.add('show');
                uploadWrapper.classList.add('error');
                this.value = '';
                uploadLabel.textContent = 'Upload Profile Picture';
                uploadLabel.style.color = '#a8a8a8';
                return;
            }
            
            // File is valid
            uploadLabel.textContent = file.name;
            uploadLabel.style.color = '#333';
            fileError.classList.remove('show');
            uploadWrapper.classList.remove('error');
        }
    });

    // Form submission validation
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        let isValid = true;
        let firstErrorField = null;

        // Reset all errors
        document.querySelectorAll('.error-message').forEach(el => el.classList.remove('show'));
        document.querySelectorAll('input').forEach(el => el.classList.remove('error'));
        document.querySelectorAll('select').forEach(el => el.classList.remove('error'));
        uploadWrapper.classList.remove('error');

        // Full Name validation
        if (fullName.value.trim() === '') {
            showError('fullName', 'fullNameError', 'Please enter your full name');
            isValid = false;
            if (!firstErrorField) firstErrorField = fullName;
        }

        // Phone Number validation
        if (phoneNumber.value.trim() === '' || !/^[0-9]+$/.test(phoneNumber.value)) {
            showError('phoneNumber', 'phoneError', 'Please enter a valid phone number (numbers only)');
            isValid = false;
            if (!firstErrorField) firstErrorField = phoneNumber;
        }

        // Email validation
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email.value)) {
            showError('email', 'emailError', 'Please enter a valid email address');
            isValid = false;
            if (!firstErrorField) firstErrorField = email;
        }

        // Home Address validation
        if (homeAddress.value.trim() === '') {
            showError('homeAddress', 'addressError', 'Please enter your home address');
            isValid = false;
            if (!firstErrorField) firstErrorField = homeAddress;
        }

        // Profile Picture validation
        if (!profilePicture.files || profilePicture.files.length === 0) {
            fileError.textContent = 'Please upload your profile picture';
            fileError.classList.add('show');
            uploadWrapper.classList.add('error');
            isValid = false;
            if (!firstErrorField) firstErrorField = uploadWrapper;
        }

        // Role validation
        if (staffRole.value === '' || staffRole.value === null) {
            showErrorSelect('staffRole', 'roleError', 'Please select a role');
            isValid = false;
            if (!firstErrorField) firstErrorField = staffRole;
        }

        // Manager validation - NEW: Check if not selected
        if (managerId.value === '' || managerId.value === null) {
            showErrorSelect('managerId', 'managerError', 'Please select a manager or choose "No Manager"');
            isValid = false;
            if (!firstErrorField) firstErrorField = managerId;
        }

        // Password validation
        if (password.value.length < 6) {
            showError('password', 'passwordError', 'Password must be at least 6 characters');
            isValid = false;
            if (!firstErrorField) firstErrorField = password;
        }

        // Confirm password validation
        if (confirmPassword.value !== password.value) {
            showError('confirmPassword', 'confirmPasswordError', 'Passwords do not match');
            isValid = false;
            if (!firstErrorField) firstErrorField = confirmPassword;
        }

        if (isValid) {
            // Show confirmation modal
            document.getElementById('confirmModal').classList.add('show');
        } else {
            // Scroll to first error
            if (firstErrorField) {
                firstErrorField.scrollIntoView({ behavior: 'smooth', block: 'center' });
                if (firstErrorField === uploadWrapper) {
                    uploadWrapper.focus();
                } else {
                    firstErrorField.focus();
                }
            }
        }
    });

    // Modal buttons
    document.getElementById('modalNo').addEventListener('click', function() {
        document.getElementById('confirmModal').classList.remove('show');
    });

    document.getElementById('modalYes').addEventListener('click', function() {
        document.getElementById('confirmModal').classList.remove('show');
        // Submit the form to servlet
        form.submit();
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

    function showErrorSelect(selectId, errorId, message) {
        document.getElementById(selectId).classList.add('error');
        const errorEl = document.getElementById(errorId);
        errorEl.textContent = message;
        errorEl.classList.add('show');
    }
</script>
</body>
</html>
