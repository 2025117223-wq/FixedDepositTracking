package com.fd.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Test Database Connection Servlet
 * Access via: http://localhost:8080/YourProject/TestDBServlet
 */
@WebServlet("/TestDBServlet")
public class TestDBServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Database Connection Test</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; padding: 40px; background: #f5f5f5; }");
        out.println(".container { background: white; padding: 30px; border-radius: 10px; max-width: 700px; margin: 0 auto; }");
        out.println("h1 { color: #1a4d5e; }");
        out.println(".success { background: #d4edda; color: #155724; padding: 15px; border-radius: 5px; margin: 10px 0; }");
        out.println(".error { background: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; margin: 10px 0; }");
        out.println(".info { background: #d1ecf1; color: #0c5460; padding: 15px; border-radius: 5px; margin: 10px 0; }");
        out.println("pre { background: #f4f4f4; padding: 10px; overflow-x: auto; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>Database Connection Test</h1>");
        
        // ============================================
        // UPDATE THESE VALUES TO MATCH YOUR DATABASE
        // ============================================
        String DB_URL = "jdbc:oracle:thin:@//Acer-Nitro-5:1521/FREEPDB1";  // Change if needed
        String DB_USER = "FD";                        // <<< CHANGE THIS
        String DB_PASSWORD = "7552525";                    // <<< CHANGE THIS
        // ============================================
        
        out.println("<div class='info'>");
        out.println("<strong>Connection Details:</strong><br>");
        out.println("URL: " + DB_URL + "<br>");
        out.println("User: " + DB_USER);
        out.println("</div>");
        
        Connection conn = null;
        
        try {
            // Step 1: Load Oracle JDBC Driver
            out.println("<p><strong>Step 1:</strong> Loading Oracle JDBC Driver...</p>");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            out.println("<div class='success'>✓ Oracle JDBC Driver loaded successfully!</div>");
            
            // Step 2: Connect to database
            out.println("<p><strong>Step 2:</strong> Connecting to database...</p>");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            out.println("<div class='success'>✓ Connected to database successfully!</div>");
            
            // Step 3: Test query
            out.println("<p><strong>Step 3:</strong> Testing query on Bank table...</p>");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM Bank");
            
            if (rs.next()) {
                int count = rs.getInt("cnt");
                out.println("<div class='success'>✓ Query successful! Found " + count + " banks in database.</div>");
            }
            
            rs.close();
            stmt.close();
            
            // Step 4: List banks
            out.println("<p><strong>Step 4:</strong> Listing banks...</p>");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT bankID, bankName FROM Bank ORDER BY bankName");
            
            out.println("<table border='1' cellpadding='10' style='border-collapse: collapse; margin-top: 10px;'>");
            out.println("<tr style='background: #1a4d5e; color: white;'><th>Bank ID</th><th>Bank Name</th></tr>");
            
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                out.println("<tr>");
                out.println("<td>" + rs.getInt("bankID") + "</td>");
                out.println("<td>" + rs.getString("bankName") + "</td>");
                out.println("</tr>");
            }
            
            if (!hasRows) {
                out.println("<tr><td colspan='2'>No banks found. Please insert sample data.</td></tr>");
            }
            
            out.println("</table>");
            
            rs.close();
            stmt.close();
            
            out.println("<br><div class='success'><strong>All tests passed! Database connection is working.</strong></div>");
            
        } catch (ClassNotFoundException e) {
            out.println("<div class='error'>");
            out.println("<strong>✗ Error:</strong> Oracle JDBC Driver not found!<br><br>");
            out.println("<strong>Solution:</strong> Make sure ojdbc8.jar is in WEB-INF/lib/<br>");
            out.println("<pre>" + e.getMessage() + "</pre>");
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<div class='error'>");
            out.println("<strong>✗ Error:</strong> " + e.getClass().getSimpleName() + "<br><br>");
            out.println("<strong>Message:</strong> " + e.getMessage() + "<br><br>");
            out.println("<strong>Possible solutions:</strong><br>");
            out.println("1. Check if Oracle database is running<br>");
            out.println("2. Verify DB_URL, DB_USER, DB_PASSWORD are correct<br>");
            out.println("3. Check if the user has permission to access the tables<br>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
            out.println("</div>");
            
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
