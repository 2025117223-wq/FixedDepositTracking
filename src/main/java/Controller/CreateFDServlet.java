package com.fd.servlet;

import com.fd.dao.BankDAO;
import com.fd.model.Bank;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * CreateFDServlet - WITH BANK LOADING FROM DATABASE
 * GET: Loads banks from database and shows CreateFD.jsp
 * POST: Stores form data in session and redirects to ApplicationForm.jsp
 * Uses: com.fd.dao.BankDAO and com.fd.model.Bank
 */
@WebServlet("/CreateFDServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 1024 * 1024 * 10,       // 10 MB
    maxRequestSize = 1024 * 1024 * 50     // 50 MB
)
public class CreateFDServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private BankDAO bankDAO;
    
    @Override
    public void init() throws ServletException {
        bankDAO = new BankDAO();
        System.out.println("========================================");
        System.out.println("✅ CreateFDServlet initialized successfully!");
        System.out.println("   Bank loading: ENABLED");
        System.out.println("   Using: com.fd.dao.BankDAO");
        System.out.println("   Model: com.fd.model.Bank");
        System.out.println("========================================");
    }
    
    /**
     * GET request - Load banks from database and show CreateFD.jsp
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("📥 CreateFDServlet - GET Request");
        System.out.println("   Loading banks from database...");
        System.out.println("========================================");
        
        try {
            // Load all banks from database
            List<Bank> bankList = bankDAO.getAllBanks();
            
            System.out.println("🏦 Loaded " + bankList.size() + " banks from database:");
            for (Bank bank : bankList) {
                System.out.println("   - " + bank.getBankName());
            }
            
            // Store bank list in request
            request.setAttribute("bankList", bankList);
            
            // Get session to check for any existing form data (from errors or back button)
            HttpSession session = request.getSession();
            
            // Check if there's existing form data from previous submission errors
            String accountNumber = (String) session.getAttribute("accountNumber");
            if (accountNumber != null) {
                // Restore previous form data
                request.setAttribute("accountNumber", accountNumber);
                request.setAttribute("referralNumber", session.getAttribute("referralNumber"));
                request.setAttribute("bankName", session.getAttribute("bankName"));
                request.setAttribute("depositAmount", session.getAttribute("depositAmount"));
                request.setAttribute("interestRate", session.getAttribute("interestRate"));
                request.setAttribute("tenure", session.getAttribute("tenure"));
                request.setAttribute("startDate", session.getAttribute("startDate"));
                request.setAttribute("certNo", session.getAttribute("certNo"));
                request.setAttribute("fdType", session.getAttribute("fdType"));
                request.setAttribute("autoRenewalStatus", session.getAttribute("autoRenewalStatus"));
                request.setAttribute("withdrawableStatus", session.getAttribute("withdrawableStatus"));
                request.setAttribute("collateralStatus", session.getAttribute("collateralStatus"));
                request.setAttribute("pledgeValue", session.getAttribute("pledgeValue"));
                
                System.out.println("📋 Restored previous form data from session");
            }
            
            System.out.println("========================================");
            System.out.println("✅ Forwarding to CreateFD.jsp");
            System.out.println("========================================");
            
            // Forward to CreateFD.jsp
            request.getRequestDispatcher("CreateFD.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("❌ Error loading banks: " + e.getMessage());
            e.printStackTrace();
            System.err.println("========================================");
            
            // If database error, create empty list and show error
            request.setAttribute("bankList", new ArrayList<Bank>());
            request.setAttribute("error", "Unable to load bank list. Please try again.");
            request.getRequestDispatcher("CreateFD.jsp").forward(request, response);
        }
    }
    
    /**
     * POST request - Store form data in session and redirect to ApplicationForm
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        System.out.println("========================================");
        System.out.println("📥 CreateFDServlet - POST Request Received");
        System.out.println("========================================");
        
        try {
            // Get all form parameters
            String accNumber = request.getParameter("accountNumber");
            String referralNumber = request.getParameter("referralNumber");
            String bankName = request.getParameter("bankName");
            String depositAmount = request.getParameter("depositAmount");
            String interestRate = request.getParameter("interestRate");
            String startDate = request.getParameter("startDate");
            String tenure = request.getParameter("tenure");
            String maturityDate = request.getParameter("maturityDate");
            String certNo = request.getParameter("fdCertificateNo");
            String fdType = request.getParameter("fdType");
            
            // Free FD specific fields
            String autoRenewalStatus = request.getParameter("autoRenewalStatus");
            String withdrawableStatus = request.getParameter("withdrawableStatus");
            
            // Pledge FD specific fields
            String pledgeValue = request.getParameter("pledgeValue");
            String collateralStatus = request.getParameter("collateralStatus");
            
            // Get reminder settings
            String reminderMaturity = request.getParameter("reminderMaturity");
            String reminderIncomplete = request.getParameter("reminderIncomplete");
            
            // ========== DEBUG LOGGING ==========
            System.out.println("📋 Form Data Received:");
            System.out.println("   Account Number: " + accNumber);
            System.out.println("   Bank Name: " + bankName);
            System.out.println("   Deposit Amount: " + depositAmount);
            System.out.println("   FD Type: " + fdType);
            System.out.println("");
            System.out.println("🔵 FREE FD Attributes:");
            System.out.println("   Auto Renewal Status: " + autoRenewalStatus);
            System.out.println("   Withdrawable Status: " + withdrawableStatus);
            System.out.println("");
            System.out.println("🟣 PLEDGE FD Attributes:");
            System.out.println("   Collateral Status: " + collateralStatus);
            System.out.println("   Pledge Value: " + pledgeValue);
            System.out.println("");
            System.out.println("🔔 Reminder Settings:");
            System.out.println("   Reminder Maturity: " + reminderMaturity);
            System.out.println("   Reminder Incomplete: " + reminderIncomplete);
            System.out.println("========================================");
            
            // Handle file upload - convert to Base64 string for session storage
            String fdCertBase64 = null;
            String fdCertFileName = null;
            Part filePart = request.getPart("fdCertificate");
            if (filePart != null && filePart.getSize() > 0) {
                InputStream inputStream = filePart.getInputStream();
                byte[] bytes = inputStream.readAllBytes();
                fdCertBase64 = Base64.getEncoder().encodeToString(bytes);
                fdCertFileName = getFileName(filePart);
                inputStream.close();
                System.out.println("📄 File uploaded: " + fdCertFileName);
            } else {
                System.out.println("📄 No file uploaded");
            }
            
            // Store all data in session
            HttpSession session = request.getSession();
            session.setAttribute("accountNumber", accNumber);
            session.setAttribute("referralNumber", referralNumber);
            session.setAttribute("bankName", bankName);
            session.setAttribute("depositAmount", depositAmount);
            session.setAttribute("interestRate", interestRate);
            session.setAttribute("startDate", startDate);
            session.setAttribute("tenure", tenure);
            session.setAttribute("maturityDate", maturityDate);
            session.setAttribute("certNo", certNo);
            session.setAttribute("fdType", fdType);
            
            // Free FD fields
            session.setAttribute("autoRenewalStatus", autoRenewalStatus);
            session.setAttribute("withdrawableStatus", withdrawableStatus);
            
            // Pledge FD fields
            session.setAttribute("pledgeValue", pledgeValue);
            session.setAttribute("collateralStatus", collateralStatus);
            
            // Store reminder settings
            session.setAttribute("reminderMaturity", reminderMaturity);
            session.setAttribute("reminderIncomplete", reminderIncomplete);
            
            // File data
            session.setAttribute("fdCertBase64", fdCertBase64);
            session.setAttribute("fdCertFileName", fdCertFileName);
            
            // Flag to indicate data is pending submission
            session.setAttribute("fdPendingSubmit", true);
            
            System.out.println("✅ All data stored in session successfully");
            System.out.println("📄 Redirecting to ApplicationForm.jsp");
            System.out.println("========================================");
            
            // Redirect to Application Form
            response.sendRedirect("ApplicationForm.jsp");
            
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("❌ Error processing form: " + e.getMessage());
            e.printStackTrace();
            System.err.println("========================================");
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("CreateFD.jsp").forward(request, response);
        }
    }
    
    /**
     * Extract file name from Part
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
