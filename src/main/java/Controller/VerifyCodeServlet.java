package Controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/VerifyCodeServlet")
public class VerifyCodeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("ForgotPassword.jsp");
            return;
        }

        String userCode = request.getParameter("code");
        if (userCode == null) userCode = "";
        userCode = userCode.trim();

        String otp = (String) session.getAttribute("fp_otp");
        Long expiry = (Long) session.getAttribute("fp_expiry");

        if (otp == null || expiry == null) {
            response.sendRedirect("ForgotPassword.jsp");
            return;
        }

        if (System.currentTimeMillis() > expiry) {
            request.setAttribute("error", "OTP expired. Please request again.");
            request.getRequestDispatcher("VerifyCode.jsp").forward(request, response);
            return;
        }

        if (!userCode.equals(otp)) {
            request.setAttribute("error", "Invalid OTP.");
            request.getRequestDispatcher("VerifyCode.jsp").forward(request, response);
            return;
        }

        // OTP correct
        session.setAttribute("fp_verified", true);

        // Redirect with message flag
        response.sendRedirect("ResetPassword.jsp?verified=true");
    }
}
