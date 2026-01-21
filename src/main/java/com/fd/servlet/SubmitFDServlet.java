package com.fd.servlet;

import com.fd.dao.FixedDepositDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Base64;

/**
 * SubmitFDServlet
 * UPDATED: Now properly saves FREE FD and PLEDGE FD attributes with correct values
 */
@WebServlet("/SubmitFDServlet")
public class SubmitFDServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private FixedDepositDAO fdDAO;
    
    @Override
    public void init() throws ServletException {
        fdDAO = new FixedDepositDAO();
        System.out.println("========================================");
        System.out.println("✅ SubmitFDServlet initialized successfully!");
        System.out.println("========================================");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        System.out.println("========================================");
        System.out.println("📥 SubmitFDServlet - Saving FD to Database");
        System.out.println("========================================");
        
        try {
            // Check if data exists in session
            Boolean pendingSubmit = (Boolean) session.getAttribute("fdPendingSubmit");
            if (pendingSubmit == null || !pendingSubmit) {
                response.sendRedirect("CreateFD.jsp");
                return;
            }
            
            // Get all data from session
            String accNumber = (String) session.getAttribute("accountNumber");
            String referralNumber = (String) session.getAttribute("referralNumber");
            String bankName = (String) session.getAttribute("bankName");
            String depositAmountStr = (String) session.getAttribute("depositAmount");
            String interestRateStr = (String) session.getAttribute("interestRate");
            String startDateStr = (String) session.getAttribute("startDate");
            String tenureStr = (String) session.getAttribute("tenure");
            String maturityDateStr = (String) session.getAttribute("maturityDate");
            String certNo = (String) session.getAttribute("certNo");
            String fdTypeForm = (String) session.getAttribute("fdType");
            
            // Free FD fields
            String autoRenewalStatus = (String) session.getAttribute("autoRenewalStatus");
            String withdrawableStatus = (String) session.getAttribute("withdrawableStatus");
            
            // Pledge FD fields
            String pledgeValueStr = (String) session.getAttribute("pledgeValue");
            String collateralStatus = (String) session.getAttribute("collateralStatus");
            
            // Get reminder settings from session
            String reminderMaturity = (String) session.getAttribute("reminderMaturity");
            String reminderIncomplete = (String) session.getAttribute("reminderIncomplete");
            
            // File data
            String fdCertBase64 = (String) session.getAttribute("fdCertBase64");
            
            // Convert fdType to database format
            String fdType = "Free".equals(fdTypeForm) ? "FREEFD" : "PLEDGEFD";
            
            // Parse numeric values
            BigDecimal depositAmount = new BigDecimal(depositAmountStr);
            BigDecimal interestRate = new BigDecimal(interestRateStr);
            int tenure = Integer.parseInt(tenureStr);
            
            // Parse dates
            Date startDate = Date.valueOf(startDateStr);
            Date maturityDate = Date.valueOf(maturityDateStr);
            
            // ========== DEBUG LOGGING ==========
            System.out.println("📋 Retrieved data from session:");
            System.out.println("   Account Number: " + accNumber);
            System.out.println("   Bank Name: " + bankName);
            System.out.println("   Deposit Amount: RM " + depositAmount);
            System.out.println("   Interest Rate: " + interestRate + "%");
            System.out.println("   Tenure: " + tenure + " months");
            System.out.println("   FD Type (Form): " + fdTypeForm);
            System.out.println("   FD Type (DB): " + fdType);
            System.out.println("");
            System.out.println("🔵 FREE FD Data from Session:");
            System.out.println("   Auto Renewal Status: " + autoRenewalStatus);
            System.out.println("   Withdrawable Status: " + withdrawableStatus);
            System.out.println("");
            System.out.println("🟣 PLEDGE FD Data from Session:");
            System.out.println("   Collateral Status: " + collateralStatus);
            System.out.println("   Pledge Value: " + pledgeValueStr);
            System.out.println("========================================");
            
            // ========================================
            // ✅ DUPLICATE CHECKING - BEFORE SAVING
            // ========================================
            System.out.println("");
            System.out.println("🔍 Checking for duplicates...");
            
            String duplicateError = fdDAO.checkForDuplicates(accNumber, referralNumber, certNo);
            
            if (duplicateError != null) {
                // Duplicates found!
                System.err.println("❌ DUPLICATE FOUND: " + duplicateError);
                System.out.println("========================================");
                
                // Store error message in session
                session.setAttribute("error", duplicateError);
                
                // Redirect back to ApplicationForm to show error
                response.sendRedirect("ApplicationForm.jsp");
                return;
            }
            
            System.out.println("✅ No duplicates found - proceeding with save");
            System.out.println("");
            // ========================================
            
            // ============================================================
            // CALCULATE EXPECTED PROFIT
            // ============================================================
            // Formula: Interest = (Principal × Rate × Tenure) / (100 × 12)
            BigDecimal interest = depositAmount.multiply(interestRate)
                                             .multiply(new BigDecimal(tenure))
                                             .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
            
            // Total Profit = Principal + Interest
            BigDecimal totalProfit = depositAmount.add(interest);
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("💰 FD PROFIT CALCULATION:");
            System.out.println("   Principal: RM " + depositAmount);
            System.out.println("   Interest Rate: " + interestRate + "%");
            System.out.println("   Tenure: " + tenure + " months");
            System.out.println("   Interest: RM " + interest);
            System.out.println("   Total Profit: RM " + totalProfit);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // Convert Base64 to InputStream for BLOB
            InputStream fdCertStream = null;
            if (fdCertBase64 != null && !fdCertBase64.isEmpty()) {
                byte[] decodedBytes = Base64.getDecoder().decode(fdCertBase64);
                fdCertStream = new ByteArrayInputStream(decodedBytes);
            }
            
            // Get or create bank
            int bankID = fdDAO.getOrCreateBank(bankName);
            
            if (bankID == -1) {
                session.setAttribute("error", "Failed to get or create bank. Please try again.");
                response.sendRedirect("ApplicationForm.jsp");
                return;
            }
            
            System.out.println("🏦 Bank ID: " + bankID);
            System.out.println("");
            
            // Insert Fixed Deposit Record to database WITH reminder settings
            System.out.println("💾 Inserting FD Record into database...");
            int fdID = fdDAO.insertFixedDeposit(
                accNumber, referralNumber, depositAmount, interestRate,
                startDate, tenure, maturityDate, fdCertStream, certNo, fdType, bankID,
                reminderMaturity, reminderIncomplete
            );
            
            if (fdID == -1) {
                session.setAttribute("error", "Failed to create Fixed Deposit. Please try again.");
                response.sendRedirect("ApplicationForm.jsp");
                return;
            }
            
            System.out.println("✅ FD Record inserted successfully! FDID: " + fdID);
            System.out.println("");
            
            // ============================================================
            // INSERT TYPE-SPECIFIC RECORD (FREE FD or PLEDGE FD)
            // ============================================================
            boolean typeInsertSuccess = false;
            
            if ("FREEFD".equals(fdType)) {
                System.out.println("🔵 Inserting FREE FD attributes...");
                System.out.println("   FDID: " + fdID);
                System.out.println("   Auto Renewal Status (from session): " + autoRenewalStatus);
                System.out.println("   Withdrawable Status (from session): " + withdrawableStatus);
                
                // FREE FD uses the values directly from session (already Y/N and Full/Partial/No)
                String autoRenewal = autoRenewalStatus != null ? autoRenewalStatus : "N";
                String withdrawable = withdrawableStatus != null ? withdrawableStatus : "No";
                
                System.out.println("   Auto Renewal Status (to DB): " + autoRenewal);
                System.out.println("   Withdrawable Status (to DB): " + withdrawable);
                
                typeInsertSuccess = fdDAO.insertFreeFD(fdID, autoRenewal, withdrawable);
                
                if (typeInsertSuccess) {
                    System.out.println("✅ FREE FD attributes inserted successfully!");
                } else {
                    System.err.println("❌ Failed to insert FREE FD attributes!");
                }
                
            } else if ("PLEDGEFD".equals(fdType)) {
                System.out.println("🟣 Inserting PLEDGE FD attributes...");
                System.out.println("   FDID: " + fdID);
                System.out.println("   Collateral Status (from session): " + collateralStatus);
                System.out.println("   Pledge Value (from session): " + pledgeValueStr);
                
                // PLEDGE FD uses the values directly from session (already Y/N)
                String collateral = collateralStatus != null ? collateralStatus : "N";
                BigDecimal pledgeValue = null;
                if (pledgeValueStr != null && !pledgeValueStr.trim().isEmpty()) {
                    pledgeValue = new BigDecimal(pledgeValueStr);
                }
                
                System.out.println("   Collateral Status (to DB): " + collateral);
                System.out.println("   Pledge Value (to DB): " + pledgeValue);
                
                typeInsertSuccess = fdDAO.insertPledgeFD(fdID, collateral, pledgeValue);
                
                if (typeInsertSuccess) {
                    System.out.println("✅ PLEDGE FD attributes inserted successfully!");
                } else {
                    System.err.println("❌ Failed to insert PLEDGE FD attributes!");
                }
            }
            
            if (!typeInsertSuccess) {
                System.err.println("⚠️ Warning: Type-specific data insert failed for FD ID: " + fdID);
            }
            
            System.out.println("");
            
            // ============================================================
            // CREATE TRANSACTION RECORD WITH PROFIT
            // ============================================================
            Integer staffID = (Integer) session.getAttribute("staffID");
            if (staffID == null) {
                staffID = 1;
                System.out.println("⚠️ Warning: No staffID in session, using default staffID = 1");
            }
            
            System.out.println("📝 Creating transaction record...");
            System.out.println("   Staff ID: " + staffID);
            System.out.println("   Expected Profit: RM " + totalProfit);
            
            // Call the method WITH profit parameter (3 parameters)
            boolean transactionCreated = fdDAO.insertFDTransaction_Create(
                fdID, 
                staffID, 
                totalProfit
            );
            
            if (transactionCreated) {
                System.out.println("✅ Transaction record created successfully!");
            } else {
                System.err.println("❌ Warning: Transaction record creation failed for FD ID: " + fdID);
            }
            
            System.out.println("");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📋 FD CREATION SUMMARY:");
            System.out.println("   FD ID: " + fdID);
            System.out.println("   Account: " + accNumber);
            System.out.println("   Type: " + fdType);
            System.out.println("   Amount: RM " + depositAmount);
            System.out.println("   Expected Profit: RM " + totalProfit);
            System.out.println("   Maturity Reminder: " + reminderMaturity);
            System.out.println("   Incomplete Reminder: " + reminderIncomplete);
            System.out.println("   Type-Specific Data: " + (typeInsertSuccess ? "✅ Saved" : "❌ Failed"));
            System.out.println("   Transaction Record: " + (transactionCreated ? "✅ Created" : "❌ Failed"));
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // Clear session data
            clearFDSessionData(session);
            System.out.println("🧹 Session data cleared");
            
            // Set success message
            session.setAttribute("fdSuccess", true);
            session.setAttribute("newFdID", fdID);
            
            System.out.println("");
            System.out.println("========================================");
            System.out.println("✅ FD CREATED SUCCESSFULLY - FDID: " + fdID);
            System.out.println("========================================");
            
            // Redirect to FD List
            response.sendRedirect("FDListServlet");
            
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("❌ Error saving Fixed Deposit: " + e.getMessage());
            e.printStackTrace();
            System.err.println("========================================");
            session.setAttribute("error", "Error saving to database: " + e.getMessage());
            response.sendRedirect("ApplicationForm.jsp");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect GET requests to CreateFD
        response.sendRedirect("CreateFD.jsp");
    }
    
    /**
     * Clear all FD-related session attributes
     */
    private void clearFDSessionData(HttpSession session) {
        session.removeAttribute("accountNumber");
        session.removeAttribute("referralNumber");
        session.removeAttribute("bankName");
        session.removeAttribute("depositAmount");
        session.removeAttribute("interestRate");
        session.removeAttribute("startDate");
        session.removeAttribute("tenure");
        session.removeAttribute("maturityDate");
        session.removeAttribute("certNo");
        session.removeAttribute("fdType");
        session.removeAttribute("autoRenewalStatus");
        session.removeAttribute("withdrawableStatus");
        session.removeAttribute("pledgeValue");
        session.removeAttribute("collateralStatus");
        session.removeAttribute("fdCertBase64");
        session.removeAttribute("fdCertFileName");
        session.removeAttribute("fdPendingSubmit");
        session.removeAttribute("reminderMaturity");
        session.removeAttribute("reminderIncomplete");
    }
}
