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
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-image: 
                linear-gradient(rgba(12, 55, 63, 0.7), rgba(12, 55, 63, 0.8)),
                url('images/signupBG.png');
            background-size: cover;
            background-position: center;
            background-attachment: fixed;
            max-height: 100vh;
            display: flex;
        }

        .container {
            display: flex;
            width: 100%;
            min-height: 100vh;
            align-items: center;
            padding: 40px;
        }

        .left-panel {
            flex: 0 0 50%;
            background: transparent;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: flex-start;
            padding: 60px 80px;
            color: white;
            position: relative;
        }

        .logo {
            width: 240px;
            height: 240px;
            position: relative;
            margin-top: -150px;
            margin-bottom: -30px;
        }

        .logo img {
            width: 100%;
            height: 100%;
            object-fit: contain;
        }

        .system-title {
            text-align: left;
            margin-bottom: -20px;
        }

        .system-title h1 {
            font-size: 4.5rem;
            font-weight: 700;
            color: #ff914d;
            line-height: 1.1;
            text-shadow: 3px 3px 6px rgba(0,0,0,0.2);
            font-family: 'Libre Baskerville', serif;
        }

        .system-description {
            max-width: 600px;
            margin-top: 35px;
        }

        .system-description p {
            font-size: 1.4rem;
            line-height: 1.6;
            color: #e0e0e0;
            font-weight: medium;
            font-family: 'Inter', sans-serif;
        }

        .right-panel {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 60px;
            background: transparent;
            position: relative;
            overflow: hidden;
            min-height: 100vh;
        }

        .right-panel::before {
            content: '';
            position: absolute;
            top: 0;
            right: 0;
            width: 130%;
            height: 100%;
            background-image: url('images/lineP.png');
            background-size: cover;
            background-position: 100% center;
            background-repeat: no-repeat;
            opacity: 0.4;
            pointer-events: none;
        }

        .login-form {
            width: 100%;
            max-width: 500px;
            position: relative;
            z-index: 1;
        }

        .form-header {
            text-align: center;
            margin-bottom: 50px;
        }

        .form-header h2 {
            font-size: 3rem;
            color: #ffffff;
            font-weight: 700;
            margin-bottom: 15px;
        }

        .form-header .divider {
            width: 100%;
            height: 4px;
            background: #1a4d5e;
            border: none;
            border-top: 3px dashed #ffffff;
            background: transparent;
        }

        .form-group {
            margin-bottom: 30px;
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
            margin-left: -10px;
            z-index: 1;
            object-fit: contain;
        }

        .password-icon {
            position: absolute;
            left: 25px;
            width: 35px;
            height: 35px;
            margin-left: -5px;
            z-index: 1;
            object-fit: contain;
        }

        input[type="email"],
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
            margin-top: 40px;
            box-shadow: 0 6px 20px rgba(255, 154, 90, 0.3);
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(255, 154, 90, 0.4);
        }

        .signup-link {
            text-align: center;
            margin-top: 25px;
            color: #ffffff;
            font-size: 16px;
        }

        .signup-link a {
            color: #ffffff;
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

        /* Success Message */
        .success-message {
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            background: #7dd3c0;
            color: #0c373f;
            padding: 15px 40px;
            border-radius: 15px;
            font-size: 18px;
            font-weight: 600;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
            z-index: 9999;
            display: none;
            animation: slideDown 0.5s ease;
            font-family: 'Inter', sans-serif;
        }

        .success-message.show {
            display: block;
        }

        .success-message.hide {
            animation: fadeOut 0.5s ease forwards;
        }

        @keyframes slideDown {
            from {
                top: -100px;
                opacity: 0;
            }
            to {
                top: 20px;
                opacity: 1;
            }
        }

        @keyframes fadeOut {
            from {
                opacity: 1;
                top: 20px;
            }
            to {
                opacity: 0;
                top: -100px;
            }
        }

        @media (max-width: 1200px) {
            .left-panel {
                padding: 40px 50px;
            }

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
                width: 120px;
                height: 120px;
            }

            .system-title h1 {
                font-size: 2.5rem;
            }

            .system-description p {
                font-size: 1rem;
            }

            .right-panel {
                padding: 40px 20px;
            }

            .submit-btn {
                width: 100%;
            }
        }
    </style>
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
                        </div>
                        <div class="error-message" id="passwordError">Please enter your password</div>
                    </div>

                    <button type="submit" class="submit-btn">Login</button>

                    <div class="signup-link">
                        Don't have an account? Click here to <a href="SignUp.jsp">Sign Up !</a>
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