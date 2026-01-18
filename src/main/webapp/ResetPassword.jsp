<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reset Password - Fixed Deposit Tracking System</title>

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Libre+Baskerville:wght@400;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="css/ResetPassword.css">
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
          <h2>Reset Password</h2>
          <hr class="divider">
        </div>

        <form id="resetForm" action="ResetPasswordServlet" method="post">
          <!-- New Password -->
          <div class="form-row">
            <div class="form-group">
              <div class="input-wrapper" style="position: relative;">
                <img src="images/icons/password.png" alt="Password Icon" class="input-icon">

                <input type="password" id="password" name="password" placeholder="New Password" required>

                <button type="button" id="togglePassword"
                        style="position: absolute; right: 25px; background: none; border: none; cursor: pointer; z-index: 2;">
                  <img id="passwordIcon" src="images/icons/showpass.png" alt="Show Password" style="width: 25px; height: 25px;">
                </button>
              </div>
            </div>
          </div>

          <!-- Confirm Password -->
          <div class="form-row">
            <div class="form-group">
              <div class="input-wrapper" style="position: relative;">
                <img src="images/icons/lock.png" alt="Confirm Password Icon" class="input-icon">

                <input type="password" id="confirm" name="confirm" placeholder="Re-enter Password" required>

                <button type="button" id="toggleConfirm"
                        style="position: absolute; right: 25px; background: none; border: none; cursor: pointer; z-index: 2;">
                  <img id="confirmIcon" src="images/icons/showpass.png" alt="Show Password" style="width: 25px; height: 25px;">
                </button>
              </div>

              <div class="helper-text">Make sure both passwords match.</div>

              <!-- Client-side error -->
              <div id="clientError" class="error-message" style="display:none;"></div>
            </div>
          </div>

          <button type="submit" class="submit-btn">Reset</button>

          <!-- Server-side messages -->
          <div class="error-message" style="${empty error ? 'display:none;' : ''}">
            ${error}
          </div>

          <div class="success-message" style="${empty success ? 'display:none;' : ''}">
            ${success}
          </div>
        </form>
      </div>
    </div>
  </div>

  <script>
    // Elements
    const form = document.getElementById('resetForm');
    const passwordField = document.getElementById('password');
    const confirmField = document.getElementById('confirm');
    const clientError = document.getElementById('clientError');

    // Toggle New Password
    const togglePasswordButton = document.getElementById("togglePassword");
    const passwordIcon = document.getElementById("passwordIcon");

    togglePasswordButton.addEventListener("click", () => {
      if (passwordField.type === "password") {
        passwordField.type = "text";
        passwordIcon.src = "images/icons/hidepass.png";
      } else {
        passwordField.type = "password";
        passwordIcon.src = "images/icons/showpass.png";
      }
    });

    // Toggle Confirm Password
    const toggleConfirmButton = document.getElementById("toggleConfirm");
    const confirmIcon = document.getElementById("confirmIcon");

    toggleConfirmButton.addEventListener("click", () => {
      if (confirmField.type === "password") {
        confirmField.type = "text";
        confirmIcon.src = "images/icons/hidepass.png";
      } else {
        confirmField.type = "password";
        confirmIcon.src = "images/icons/showpass.png";
      }
    });

    function showClientError(message) {
      clientError.textContent = message;
      clientError.style.display = "block";
    }

    function clearClientError() {
      clientError.textContent = "";
      clientError.style.display = "none";
    }

    // Live validation: passwords must match
    function validateMatch() {
      if (passwordField.value && confirmField.value && passwordField.value !== confirmField.value) {
        showClientError("Password and Confirm Password do not match.");
        return false;
      }
      clearClientError();
      return true;
    }

    passwordField.addEventListener("input", validateMatch);
    confirmField.addEventListener("input", validateMatch);

    // Submit validation
    form.addEventListener("submit", (e) => {
      clearClientError();

      if (passwordField.value !== confirmField.value) {
        e.preventDefault();
        showClientError("Password and Confirm Password must match.");
      }
    });
  </script>
</body>
</html>
