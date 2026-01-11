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
    <!-- Success Message -->
    <div class="success-message" id="successMessage">
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

                <form id="loginForm" action="#" method="POST">
                    <div class="form-group">
                        <div class="input-wrapper">
                            <img src="images/icons/user.jpg" alt="Email" class="input-icon">
                            <input type="email" id="email" name="email" placeholder="Your Email" required>
                        </div>
                        <div class="error-message" id="emailError">Please enter a valid email address</div>
                    </div>


                    <div class="form-group">
					    <div class="input-wrapper">
					        <img src="images/icons/password.png" alt="Password" class="password-icon">
					        <input type="password" id="password" name="password" placeholder="Your Password" required>
					        <button type="button" id="togglePassword" style="position: absolute; right: 25px; background: none; border: none; cursor: pointer; z-index: 2;">
					            <img id="passwordIcon" src="images/icons/showpass.png" alt="Show Password" style="width: 25px; height: 25px;">
					        </button>
					    </div>
					    <div class="error-message" id="passwordError">Please enter your password</div>
					    
					    <!-- Forgot Password Link -->
					    <div class="forgot-password-link">
					        <a href="ForgotPassword.jsp">Forgot Password ?</a>
					    </div>
					</div>
                    

                    <button type="submit" class="submit-btn">Login</button>

                    <div class="signup-link">
                        Don't have an account? Click here to <a href="SignUp.jsp">Sign Up </a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Check for success message from URL parameter
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('signup') === 'success') {
            const successMessage = document.getElementById('successMessage');
            successMessage.classList.add('show');
            
            // Hide message after 5 seconds with animation
            setTimeout(() => {
                successMessage.classList.add('hide');
                
                // Remove from DOM after animation completes
                setTimeout(() => {
                    successMessage.classList.remove('show', 'hide');
                    // Clean URL by removing the parameter
                    window.history.replaceState({}, document.title, window.location.pathname);
                }, 500); // Wait for fadeOut animation (0.5s)
            }, 5000);
        }

        // Form validation
        const form = document.getElementById('loginForm');
        const email = document.getElementById('email');
        const password = document.getElementById('password');
        const showPasswordButton = document.getElementById('showPassword');

        // Get elements
        const togglePasswordButton = document.getElementById("togglePassword");
        const passwordField = document.getElementById("password");
        const passwordIcon = document.getElementById("passwordIcon");
	
        // Toggle password visibility
        togglePasswordButton.addEventListener("click", () => {
            if (passwordField.type === "password") {
                passwordField.type = "text";
                passwordIcon.src = "images/icons/hidepass.png"; // Switch to "hide" icon
            } else {
                passwordField.type = "password";
                passwordIcon.src = "images/icons/showpass.png"; // Switch back to "show" icon
            }
        });

        // Form submission validation
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            let isValid = true;

            // Reset all errors
            document.querySelectorAll('.error-message').forEach(el => el.classList.remove('show'));
            document.querySelectorAll('input').forEach(el => el.classList.remove('error'));

            // Email validation
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(email.value)) {
                showError('email', 'emailError', 'Please enter a valid email address');
                isValid = false;
            }

            // Password validation
            if (password.value.trim() === '') {
                showError('password', 'passwordError', 'Please enter your password');
                isValid = false;
            }

            if (isValid) {
                // Redirect to dashboard
                window.location.href = 'Dashboard.jsp';
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
