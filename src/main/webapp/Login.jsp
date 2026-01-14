<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Fixed Deposit Tracking System</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Libre+Baskerville:wght@400;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/Login.css">
</head>
<style>
/* ================= RESET ================= */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* ================= BODY ================= */
body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background-image:
        linear-gradient(rgba(12, 55, 63, 0.7), rgba(12, 55, 63, 0.8)),
        url('../images/signupBG.png');
    background-size: cover;
    background-position: center;
    background-attachment: fixed;
    min-height: 100vh;
    display: flex;
}

/* ================= LAYOUT ================= */
.container {
    display: flex;
    width: 100%;
    min-height: 100vh;
    align-items: center;
    padding: 40px;
}

/* ================= LEFT PANEL ================= */
.left-panel {
    flex: 0 0 50%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 60px 80px;
    color: white;
}

.logo {
    width: 240px;
    height: 240px;
    margin-top: -150px;
    margin-bottom: -30px;
}

.logo img {
    width: 100%;
    height: 100%;
    object-fit: contain;
}

.system-title h1 {
    font-size: 4.5rem;
    font-weight: 700;
    color: #ff914d;
    line-height: 1.1;
    font-family: 'Libre Baskerville', serif;
}

/* ================= RIGHT PANEL ================= */
.right-panel {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 60px;
    position: relative;
}

.login-form {
    width: 100%;
    max-width: 500px;
}

/* ================= HEADER ================= */
.form-header {
    text-align: center;
    margin-bottom: 50px;
}

.form-header h2 {
    font-size: 3rem;
    color: #ffffff;
    font-weight: 700;
}

.divider {
    border-top: 3px dashed #ffffff;
    margin-top: 15px;
}

/* ================= FORM ================= */
.form-group {
    margin-bottom: 30px;
}

.input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
}

/* ================= ICONS ================= */
.input-icon,
.password-icon {
    position: absolute;
    left: 25px;
    width: 40px;
    height: 40px;
    object-fit: contain;
    z-index: 2;
}

/* ================= INPUTS ================= */
input[type="email"],
input[type="password"],
input[type="text"] {
    width: 100%;
    padding: 20px 25px 20px 70px;
    border: 2px solid #d0d0d0;
    border-radius: 50px;
    font-size: 16px;
    background: #fafafa;
    color: #333;
    transition: all 0.3s ease;
}

input:focus {
    outline: none;
    border-color: #1a4d5e;
    background: #fff;
}

input::placeholder {
    color: #a8a8a8;
}

/* ================= FORGOT PASSWORD ================= */
.forgot-password-link {
    text-align: right;
    margin-top: 10px;
}

.forgot-password-link a {
    color: #ffffff;
    font-size: 0.9rem;
    font-weight: 700;
    text-decoration: underline !important;
    text-underline-offset: 3px;
}

.forgot-password-link a:hover {
    text-decoration: none;
}

/* ================= BUTTON ================= */
.submit-btn {
    width: 100%;
    padding: 20px;
    background: linear-gradient(135deg, #ff9a5a, #ff8547);
    border: none;
    border-radius: 50px;
    color: white;
    font-size: 20px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-top: 20px;
}

.submit-btn:hover {
    transform: translateY(-2px);
}

/* ================= SIGN UP LINK ================= */
.signup-link {
    text-align: center;
    margin-top: 25px;
    color: #ffffff;
    font-size: 16px;
}

.signup-link a {
    color: #ffffff;
    font-weight: 700;
    text-decoration: underline !important;
    text-underline-offset: 3px;
}

.signup-link a:hover {
    text-decoration: none;
}

/* ================= ERROR ================= */
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

/* ================= RESPONSIVE ================= */
@media (max-width: 968px) {
    .container {
        flex-direction: column;
    }

    .left-panel {
        padding: 40px 20px;
    }

    .logo {
        width: 120px;
        height: 120px;
    }

    .system-title h1 {
        font-size: 2.5rem;
    }

    .right-panel {
        padding: 40px 20px;
    }
    
    /* ================= SUCCESS MESSAGE ================= */
.success-message{
    position: fixed;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    padding: 12px 18px;
    border-radius: 12px;
    font-weight: 700;
    z-index: 9999;

    background: rgba(46, 204, 113, 0.95);
    color: #fff;

    opacity: 0;
    visibility: hidden;
    pointer-events: none;
    transition: opacity 0.3s ease, transform 0.3s ease;
}

.success-message.show{
    opacity: 1;
    visibility: visible;
    transform: translateX(-50%) translateY(0);
}

.success-message.hide{
    opacity: 0;
    visibility: hidden;
    transform: translateX(-50%) translateY(-10px);
}

/* ================= SERVER ERROR (from servlet) ================= */
.server-error{
    background: rgba(231, 76, 60, 0.18);
    border: 1px solid rgba(231, 76, 60, 0.7);
    color: #ffffff;
    font-weight: 600;
}

}


</style>
<body>
<%
    // Read server error only if this page is loaded via POST (prevents error showing on first GET)
    String error = (String) request.getAttribute("error");
    boolean isPost = "POST".equalsIgnoreCase(request.getMethod());

    // Preserve email value if servlet sets it; fallback to empty
    Object emailObj = request.getAttribute("emailValue");
    String emailValue = emailObj != null ? emailObj.toString() : "";
%>

    <!-- Success Message (hidden by default; show only when ?signup=success) -->
    <div class="success-message" id="successMessage" style="display:none;">
        User Account Created Successfully!
    </div>

    <div class="container">
        <div class="left-panel">
            <div class="logo">
                <img src="images/Logo.png" alt="Infra Desa Johor Logo">
            </div>
            <div class="system-title">
                <h1>Fixed Deposit<br>Tracking System</h1>
            </div>
            <div class="system-description">
                <p>A digital platform designed to simplify fixed deposit tracking and monitoring.</p>
            </div>
        </div>

        <div class="right-panel">
            <div class="login-form">
                <div class="form-header">
                    <h2>Login Account</h2>
                    <hr class="divider">
                </div>

                <!-- SERVER ERROR MESSAGE (POST only) -->
                <%
                    if (isPost && error != null && !error.trim().isEmpty()) {
                %>
                    <div class="server-error" style="margin-bottom: 12px; padding: 10px; border-radius: 8px;">
                        <%= error %>
                    </div>
                <%
                    }
                %>

                <form id="loginForm" action="<%= request.getContextPath() %>/LoginServlet" method="POST">
                    <div class="form-group">
                        <div class="input-wrapper">
                            <img src="images/icons/user.jpg" alt="Email" class="input-icon">
                            <input type="email" id="email" name="email" placeholder="Your Email" required
                                   value="<%= emailValue %>">
                        </div>
                        <div class="error-message" id="emailError">Please enter a valid email address</div>
                    </div>

                    <div class="form-group">
                        <div class="input-wrapper" style="position: relative;">
                            <img src="images/icons/password.png" alt="Password" class="password-icon">
                            <input type="password" id="password" name="password" placeholder="Your Password" required>
                            <button type="button" id="togglePassword"
                                    style="position: absolute; right: 25px; background: none; border: none; cursor: pointer; z-index: 2;">
                                <img id="passwordIcon" src="images/icons/showpass.png" alt="Show Password" style="width: 25px; height: 25px;">
                            </button>
                        </div>

                        <div class="error-message" id="passwordError">Please enter your password</div>

                        <div class="forgot-password-link">
                            <a href="ForgotPassword.jsp">Forgot Password ?</a>
                        </div>
                    </div>

                    <button type="submit" class="submit-btn">Login</button>

                    <div class="signup-link">
                        Don't have an account? Click here to <a href="SignUp.jsp">Sign Up</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

<script>
    // success message (signup)
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('signup') === 'success') {
        const successMessage = document.getElementById('successMessage');

        // show (because default is hidden)
        successMessage.style.display = 'block';
        successMessage.classList.add('show');

        setTimeout(() => {
            successMessage.classList.add('hide');
            setTimeout(() => {
                successMessage.classList.remove('show', 'hide');
                successMessage.style.display = 'none'; // hide again
                window.history.replaceState({}, document.title, window.location.pathname);
            }, 500);
        }, 5000);
    }

    // Elements
    const form = document.getElementById('loginForm');
    const email = document.getElementById('email');
    const passwordField = document.getElementById('password');

    const togglePasswordButton = document.getElementById("togglePassword");
    const passwordIcon = document.getElementById("passwordIcon");

    // Toggle password visibility
    togglePasswordButton.addEventListener("click", () => {
        if (passwordField.type === "password") {
            passwordField.type = "text";
            passwordIcon.src = "images/icons/hidepass.png";
        } else {
            passwordField.type = "password";
            passwordIcon.src = "images/icons/showpass.png";
        }
    });

    // Form validation - prevent submit ONLY if invalid
    form.addEventListener('submit', function(e) {
        let isValid = true;

        document.querySelectorAll('.error-message').forEach(el => el.classList.remove('show'));
        document.querySelectorAll('input').forEach(el => el.classList.remove('error'));

        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email.value)) {
            showError('email', 'emailError', 'Please enter a valid email address');
            isValid = false;
        }

        if (passwordField.value.trim() === '') {
            showError('password', 'passwordError', 'Please enter your password');
            isValid = false;
        }

        if (!isValid) e.preventDefault();
        // if valid -> POST to servlet
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
