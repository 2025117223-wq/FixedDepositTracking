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
    String error = (String) request.getAttribute("error");
    boolean isPost = "POST".equalsIgnoreCase(request.getMethod());

    Object emailObj = request.getAttribute("emailValue");
    String emailValue = emailObj != null ? emailObj.toString() : "";
%>

    <!-- Success Message (signup) -->
    <div class="success-message" id="successMessage" style="display:none;">
        User Account Created Successfully!
    </div>

    <!-- Success Message (reset password) -->
    <div class="success-message" id="resetSuccessMessage" style="display:none;">
        Your password has been updated successfully. Please log in.
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

                <form id="loginForm">
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
    const urlParams = new URLSearchParams(window.location.search);

    function showSuccessMessage(elementId) {
        const el = document.getElementById(elementId);
        if (!el) return;

        el.style.display = 'block';
        el.classList.add('show');

        setTimeout(() => {
            el.classList.add('hide');
            setTimeout(() => {
                el.classList.remove('show', 'hide');
                el.style.display = 'none';
                // remove query params after showing
                window.history.replaceState({}, document.title, window.location.pathname);
            }, 500);
        }, 5000);
    }

    // success message (signup)
    if (urlParams.get('signup') === 'success') {
        showSuccessMessage('successMessage');
    }

    // success message (reset password)
    if (urlParams.get('reset') === 'success') {
        showSuccessMessage('resetSuccessMessage');
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

    // Form validation
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
        else {
            e.preventDefault(); // prevent form submission and call the login API
            const loginData = {
                username: email.value,
                password: passwordField.value
            };

            fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
            })
            .then(response => response.json())
            .then(data => {
                if (data.token) {
                    // Store token in session storage
                    sessionStorage.setItem('jwtToken', data.token);
                    window.location.href = "Dashboard.jsp"; // Redirect to dashboard upon successful login
                } else {
                    // Show error message if login fails
                    alert('Login failed');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Login failed');
            });
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
