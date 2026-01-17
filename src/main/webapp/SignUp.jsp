<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="Util.DBConn" %>

<%
    List<Map<String, String>> managers = new ArrayList<>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        conn = DBConn.getConnection();

        String sqlManagers =
            "SELECT staffid, staffname " +
            "FROM staff " +
            "WHERE staffstatus = 'ACTIVE' " +
            "AND staffrole = 'Administration' " +
            "ORDER BY staffname";

        ps = conn.prepareStatement(sqlManagers);
        rs = ps.executeQuery();

        while (rs.next()) {
            Map<String, String> m = new HashMap<>();
            m.put("id", rs.getString("staffid"));
            m.put("name", rs.getString("staffname"));
            managers.add(m);
        }
    } catch (Exception e) {
        // out.println("Error loading managers: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (Exception ex) {}
        try { if (ps != null) ps.close(); } catch (Exception ex) {}
        try { if (conn != null) conn.close(); } catch (Exception ex) {}
    }
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

    <link rel="stylesheet" href="css/SignUp.css">
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

            <form id="signupForm" action="SignUpServlet" method="POST" enctype="multipart/form-data">

                <!-- Full Name (Full Width) -->
                <div class="form-row fullwidth">
                    <div class="form-group">
                        <div class="input-wrapper">
                            <img src="images/icons/user.jpg" alt="User" class="input-icon">
                            <input type="text" id="fullName" name="fullName" placeholder="Full Name" required>
                        </div>
                        <div class="error-message" id="fullNameError">Please enter your full name</div>
                    </div>
                </div>

                <!-- Row 1: Phone | Email -->
                <div class="form-row">
                    <div class="form-group">
                        <div class="input-wrapper">
                            <img src="images/icons/phone.png" alt="Phone" class="phone-icon">
                            <input type="tel" id="phoneNumber" name="phoneNumber" placeholder="Phone Number" required pattern="[0-9]+">
                        </div>
                        <div class="error-message" id="phoneError">Please enter a valid phone number (numbers only)</div>
                    </div>

                    <div class="form-group">
                        <div class="input-wrapper">
                            <img src="images/icons/email.png" alt="Email" class="input-icon">
                            <input type="email" id="email" name="email" placeholder="Email Address" required>
                        </div>
                        <div class="error-message" id="emailError">Please enter a valid email address</div>
                    </div>
                </div>

                <!-- Row 2: Address | Profile Picture -->
                <div class="form-row">
                    <div class="form-group fullwidth">
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

                <!-- Row 3: Role | Manager -->
                <div class="form-row">
                    <div class="form-group">
                        <div class="input-wrapper select-wrapper">
                            <img src="images/icons/role.png" alt="Role" class="role-icon">
                            <select id="staffRole" name="staffRole" required>
                                <option value="" disabled selected>Select Role</option>
                                <option value="Administration">Administration</option>
                                <option value="Finance Executive">Finance Executive</option>
                                <option value="Finance Staff">Finance Staff</option>
                            </select>
                        </div>
                        <div class="error-message" id="roleError">Please select a role</div>
                    </div>

                    <div class="form-group">
                        <div class="input-wrapper select-wrapper">
                            <img src="images/icons/role.png" alt="Manager" class="role-icon">

                            <!-- ✅ Manager OPTIONAL -->
                            <select id="managerID" name="managerID">
                                <option value="" selected>No Manager</option>
                                <% if (managers != null && !managers.isEmpty()) { %>
                                    <% for (Map<String, String> m : managers) { %>
                                        <option value="<%= m.get("id") %>"><%= m.get("name") %></option>
                                    <% } %>
                                <% } %>
                            </select>
                        </div>
                        <div class="error-message" id="managerError">Please select a manager</div>
                    </div>
                </div>

                <!-- Row 4: Password | Re-confirm Password -->
                <div class="form-row">
                    <div class="form-group">
                        <div class="input-wrapper" style="position: relative;">
                            <img src="images/icons/password.png" alt="Password" class="password-icon">
                            <input type="password" id="password" name="password" placeholder="Password" required minlength="8">

                            <button type="button" id="togglePassword"
                                    style="position:absolute; right:25px; background:none; border:none; cursor:pointer; z-index:2;">
                                <img id="passwordIcon" src="images/icons/showpass.png" alt="Show Password" style="width:25px; height:25px;">
                            </button>
                        </div>
                        <div class="error-message" id="passwordError">Password must be at least 8 characters</div>
                    </div>

                    <div class="form-group">
                        <div class="input-wrapper" style="position: relative;">
                            <img src="images/icons/password.png" alt="Confirm Password" class="password-icon">
                            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Re-confirm Password" required minlength="8">

                            <button type="button" id="toggleConfirmPassword"
                                    style="position:absolute; right:25px; background:none; border:none; cursor:pointer; z-index:2;">
                                <img id="confirmPasswordIcon" src="images/icons/showpass.png" alt="Show Password" style="width:25px; height:25px;">
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
    // Form elements
    const form = document.getElementById('signupForm');
    const fullName = document.getElementById('fullName');
    const phoneNumber = document.getElementById('phoneNumber');
    const email = document.getElementById('email');
    const homeAddress = document.getElementById('homeAddress');
    const profilePicture = document.getElementById('profilePicture');
    const staffRole = document.getElementById('staffRole');
    const managerID = document.getElementById('managerID');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');

    // Toggle password elements
    const togglePasswordButton = document.getElementById("togglePassword");
    const passwordIcon = document.getElementById("passwordIcon");
    const toggleConfirmPasswordButton = document.getElementById("toggleConfirmPassword");
    const confirmPasswordIcon = document.getElementById("confirmPasswordIcon");

    // Phone number validation - only numbers
    phoneNumber.addEventListener('input', function () {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    // Toggle Password
    togglePasswordButton.addEventListener("click", () => {
        if (password.type === "password") {
            password.type = "text";
            passwordIcon.src = "images/icons/hidepass.png";
        } else {
            password.type = "password";
            passwordIcon.src = "images/icons/showpass.png";
        }
    });

    // Toggle Confirm Password
    toggleConfirmPasswordButton.addEventListener("click", () => {
        if (confirmPassword.type === "password") {
            confirmPassword.type = "text";
            confirmPasswordIcon.src = "images/icons/hidepass.png";
        } else {
            confirmPassword.type = "password";
            confirmPasswordIcon.src = "images/icons/showpass.png";
        }
    });

    // File upload label + validation
    profilePicture.addEventListener('change', function (e) {
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

            if (file.size > 5 * 1024 * 1024) { // 5MB
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
        }
    });

    function showError(inputId, errorId, message) {
        document.getElementById(inputId).classList.add('error');
        const errorEl = document.getElementById(errorId);
        errorEl.textContent = message;
        errorEl.classList.add('show');
    }

    // Form submit validation + modal
    form.addEventListener('submit', function (e) {
        e.preventDefault();
        let isValid = true;

        // Reset errors (except server error)
        document.querySelectorAll('.error-message:not(.server-error)').forEach(el => el.classList.remove('show'));
        document.querySelectorAll('input, select').forEach(el => el.classList.remove('error'));

        // Full Name
        if (fullName.value.trim() === '') {
            showError('fullName', 'fullNameError', 'Please enter your full name');
            isValid = false;
        }

        // Phone
        if (phoneNumber.value.trim() === '' || !/^[0-9]+$/.test(phoneNumber.value)) {
            showError('phoneNumber', 'phoneError', 'Please enter a valid phone number (numbers only)');
            isValid = false;
        }

        // Email
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email.value)) {
            showError('email', 'emailError', 'Please enter a valid email address');
            isValid = false;
        }

        // Address
        if (homeAddress.value.trim() === '') {
            showError('homeAddress', 'addressError', 'Please enter your home address');
            isValid = false;
        }

        // Profile picture
        if (!profilePicture.files || profilePicture.files.length === 0) {
            document.getElementById('fileError').classList.add('show');
            isValid = false;
        }

        // Role
        if (staffRole.value === '') {
            showError('staffRole', 'roleError', 'Please select a role');
            isValid = false;
        }

        // ✅ Manager validation removed (manager optional)

        // Password
        if (password.value.length < 8) {
            showError('password', 'passwordError', 'Password must be at least 8 characters');
            isValid = false;
        }

        // Confirm password
        if (confirmPassword.value.length < 8) {
            showError('confirmPassword', 'confirmPasswordError', 'Password must be at least 8 characters');
            isValid = false;
        } else if (confirmPassword.value !== password.value) {
            showError('confirmPassword', 'confirmPasswordError', 'Passwords do not match');
            isValid = false;
        }

        if (isValid) {
            document.getElementById('confirmModal').classList.add('show');
        }
    });

    // Modal buttons
    document.getElementById('modalNo').addEventListener('click', function () {
        document.getElementById('confirmModal').classList.remove('show');
    });

    document.getElementById('modalYes').addEventListener('click', function () {
        document.getElementById('confirmModal').classList.remove('show');
        form.submit(); // ✅ submit to servlet
    });

    // Close modal when clicking outside
    document.getElementById('confirmModal').addEventListener('click', function (e) {
        if (e.target === this) {
            this.classList.remove('show');
        }
    });
</script>

</body>
</html>
