<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Verify Code - Fixed Deposit Tracking System</title>

  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Libre+Baskerville:wght@400;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="css/VerifyCode.css">
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
          <h2>Verification Code</h2>
          <hr class="divider">
        </div>

        <!-- Success message from redirect (ForgotPasswordServlet -> VerifyCode.jsp?sent=true) -->
        <c:if test="${param.sent == 'true'}">
          <div class="success-message" style="margin-bottom: 12px;">
            The verification code has been sent to your email. Please check it.
          </div>
        </c:if>

        <form action="VerifyCodeServlet" method="post">
          <div class="form-row">
            <div class="form-group">
              <div class="input-wrapper">
                <img src="images/icons/email.png" alt="Email Icon" class="input-icon">
                <input type="text" name="code" placeholder="Enter OTP" required>
              </div>

              <div class="helper-text">Please enter the verification code sent to your email.</div>
            </div>
          </div>

          <button type="submit" class="submit-btn">Verify</button>

          <!-- Server-side messages (from VerifyCodeServlet forward) -->
          <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
          </c:if>

          <c:if test="${not empty success}">
            <div class="success-message">${success}</div>
          </c:if>
        </form>
      </div>
    </div>
  </div>
</body>
</html>
