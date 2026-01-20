package com.fd.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Simple Test Servlet to verify servlet configuration
 * Access via: http://localhost:8080/YourProject/TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html>");
        out.println("<head><title>Test Servlet</title></head>");
        out.println("<body>");
        out.println("<h1>Servlet is working!</h1>");
        out.println("<p>If you see this, servlets are configured correctly.</p>");
        out.println("<p>Time: " + new java.util.Date() + "</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
