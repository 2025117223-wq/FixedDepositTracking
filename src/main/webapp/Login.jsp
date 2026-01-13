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
