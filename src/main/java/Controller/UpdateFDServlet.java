package com.fd.servlet;

import com.fd.dao.FixedDepositDAO;
import com.fd.model.FixedDepositRecord;
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
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * UpdateFDServlet - WITH COMPLETE REINVESTMENT FUNCTIONALITY
 * Handles loading and updating Fixed Deposit records
 * NOW INCLUDES: Complete reinvestment with new FD creation
 */
@WebServlet("/UpdateFDServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
public class UpdateFDServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private FixedDepositDAO fdDAO;
    private BankDAO bankDAO;
    
    @Override
    public void init() throws ServletException {
        fdDAO = new FixedDepositDAO();
        bankDAO = new BankDAO();
        System.out.println("UpdateFDServlet initialized successfully!");
    }
    
    /**
     * GET request - Load FD data and forward to UpdateFD.jsp
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fdIdParam = request.getParameter("id");
        
        if (fdIdParam == null || fdIdParam.isEmpty()) {
            response.sendRedirect("FDListServlet");
            return;
        }
        
        try {
            // Remove "FD" prefix if present
            String fdIdClean = fdIdParam.replace("FD", "").trim();
            int fdID = Integer.parseInt(fdIdClean);
            
            // Get FD record from database
            FixedDepositRecord fd = fdDAO.getFixedDepositById(fdID);
            
            if (fd == null) {
                request.setAttribute("error", "Fixed Deposit not found: " + fdIdParam);
                request.getRequestDispatcher("FDListServlet").forward(request, response);
                return;
            }
            
            // Get type-specific data
            if ("FREEFD".equals(fd.getFdType())) {
                java.util.Map<String, String> freeData = fdDAO.getFreeFDData(fdID);
                if (freeData != null) {
                    request.setAttribute("autoRenewalStatus", freeData.get("autoRenewalStatus"));
                    request.setAttribute("withdrawableStatus", freeData.get("withdrawableStatus"));
                }
            } else if ("PLEDGEFD".equals(fd.getFdType())) {
                java.util.Map<String, Object> pledgeData = fdDAO.getPledgeFDData(fdID);
                if (pledgeData != null) {
                    request.setAttribute("collateralStatus", pledgeData.get("collateralStatus"));
                    request.setAttribute("pledgeValue", pledgeData.get("pledgeValue"));
                }
            }
            
            // Get reminder settings from database
            java.util.Map<String, String> reminderData = fdDAO.getReminderSettings(fdID);
            if (reminderData != null) {
                request.setAttribute("reminderMaturity", reminderData.get("reminderMaturity"));
                request.setAttribute("reminderIncomplete", reminderData.get("reminderIncomplete"));
            } else {
                request.setAttribute("reminderMaturity", "off");
                request.setAttribute("reminderIncomplete", "off");
            }
            
            // Load all banks from database for dropdown
            try {
                java.util.List<Bank> bankList = bankDAO.getAllBanks();
                request.setAttribute("bankList", bankList);
                System.out.println("🏦 Loaded " + bankList.size() + " banks for UpdateFD dropdown");
            } catch (Exception e) {  // ← FIXED: Changed from java.sql.SQLException to Exception
                System.err.println("⚠️ Error loading banks: " + e.getMessage());
                request.setAttribute("bankList", new java.util.ArrayList<Bank>());
            }
            
            // Set FD data as request attribute
            request.setAttribute("fd", fd);
            
            // Forward to UpdateFD.jsp
            request.getRequestDispatcher("UpdateFD.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid FD ID: " + fdIdParam);
            response.sendRedirect("FDListServlet");
        }
    }
    
    /**
     * POST request - Update FD data in database OR process reinvestment
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        
        try {
            // Get form parameters
            String fdIdParam = request.getParameter("fdID");
            String fdIdClean = fdIdParam.replace("FD", "").trim();
            int fdID = Integer.parseInt(fdIdClean);
            
            String accNumber = request.getParameter("accountNumber");
            String referralNumber = request.getParameter("referralNumber");
            String bankName = request.getParameter("bankName");
            String depositAmountStr = request.getParameter("depositAmount");
            String interestRateStr = request.getParameter("interestRate");
            String startDateStr = request.getParameter("startDate");
            String tenureStr = request.getParameter("tenure");
            String maturityDateStr = request.getParameter("maturityDate");
            String certNo = request.getParameter("certificateNo");
            String fdTypeForm = request.getParameter("fdType");
            String status = request.getParameter("fdStatus");
            
            // Free FD fields
            String autoRenewalStatus = request.getParameter("autoRenewalStatus");
            String withdrawableStatus = request.getParameter("withdrawableStatus");
            
            // Pledge FD fields
            String pledgeValueStr = request.getParameter("pledgeValue");
            String collateralStatus = request.getParameter("collateralStatus");
            
            // Get reminder settings
            String reminderMaturity = request.getParameter("reminderMaturity");
            String reminderIncomplete = request.getParameter("reminderIncomplete");
            
            // Transaction fields
            String transactionType = request.getParameter("transactionType");
            String transactionDateStr = request.getParameter("transactionDate");
            String withdrawAmountStr = request.getParameter("withdrawAmount");
            
            // Convert fdType to database format
            String fdType = "Free".equals(fdTypeForm) ? "FREEFD" : "PLEDGEFD";
            
            // Parse numeric values
            BigDecimal depositAmount = new BigDecimal(depositAmountStr);
            BigDecimal interestRate = new BigDecimal(interestRateStr);
            int tenure = Integer.parseInt(tenureStr);
            
            // Parse dates
            java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
            java.sql.Date maturityDate = java.sql.Date.valueOf(maturityDateStr);
            
            // Handle file upload
            InputStream fdCertStream = null;
            Part filePart = request.getPart("fdCertificate");
            if (filePart != null && filePart.getSize() > 0) {
                fdCertStream = filePart.getInputStream();
            }
            
            // Get or create bank
            int bankID = fdDAO.getOrCreateBank(bankName);
            
            if (bankID == -1) {
                request.setAttribute("error", "Failed to get or create bank.");
                request.setAttribute("fd", fdDAO.getFixedDepositById(fdID));
                request.getRequestDispatcher("UpdateFD.jsp").forward(request, response);
                return;
            }
            
            // Convert status to database format
            String dbStatus = status.toUpperCase();
            
            // Update Fixed Deposit Record with reminder settings
            boolean updateSuccess = fdDAO.updateFixedDeposit(
                fdID, accNumber, referralNumber, depositAmount, interestRate,
                startDate, tenure, maturityDate, fdCertStream, certNo, fdType, dbStatus, bankID,
                reminderMaturity, reminderIncomplete
            );
            
            if (!updateSuccess) {
                request.setAttribute("error", "Failed to update Fixed Deposit.");
                request.setAttribute("fd", fdDAO.getFixedDepositById(fdID));
                request.getRequestDispatcher("UpdateFD.jsp").forward(request, response);
                return;
            }
            
            
            // Update remainingBalance ONLY if status is CHANGING to MATURED (not already MATURED)
            FixedDepositRecord currentFD = fdDAO.getFixedDepositById(fdID);
            String previousStatus = currentFD.getStatus();
            
            if ("MATURED".equals(dbStatus) && !"MATURED".equals(previousStatus)) {
                // Status is CHANGING from something else to MATURED
                // Initialize the balance to maturity amount ONLY on first maturity
                BigDecimal interest = depositAmount.multiply(interestRate)
                                                  .multiply(new BigDecimal(tenure))
                                                  .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal maturityAmount = depositAmount.add(interest);
                
                boolean balanceUpdated = fdDAO.updateFDBalance(fdID, maturityAmount);
                
                if (balanceUpdated) {
                    System.out.println("✅ Status changed to MATURED - Balance initialized to RM " + maturityAmount);
                } else {
                    System.err.println("⚠️ Failed to update balance for FD" + fdID);
                }
            } else if ("MATURED".equals(dbStatus) && "MATURED".equals(previousStatus)) {
                System.out.println("ℹ️ FD already MATURED - Skipping balance initialization (preserving withdrawals)");
            }
            
            // Update type-specific record
            if ("FREEFD".equals(fdType)) {
                String autoRenewal = "Yes".equals(autoRenewalStatus) ? "Y" : "N";
                String withdrawable = withdrawableStatus;
                fdDAO.updateFreeFD(fdID, autoRenewal, withdrawable);
            } else if ("PLEDGEFD".equals(fdType)) {
                String collateral = "Yes".equals(collateralStatus) || "Active".equals(collateralStatus) ? "Y" : "N";
                BigDecimal pledgeValue = null;
                if (pledgeValueStr != null && !pledgeValueStr.trim().isEmpty()) {
                    pledgeValue = new BigDecimal(pledgeValueStr);
                }
                fdDAO.updatePledgeFD(fdID, collateral, pledgeValue);
            }
            
            // Handle transaction creation if status is MATURED
            if ("MATURED".equals(dbStatus) && transactionType != null && !transactionType.isEmpty()) {
                
                // Get staffID from session
                Integer staffID = (Integer) session.getAttribute("staffID");
                if (staffID == null) {
                    staffID = 1;
                    System.out.println("Warning: No staffID in session, using default staffID = 1");
                }
                
                // Parse transaction date
                Timestamp transactionTimestamp = null;
                if (transactionDateStr != null && !transactionDateStr.isEmpty()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(transactionDateStr);
                    transactionTimestamp = new Timestamp(parsedDate.getTime());
                } else {
                    transactionTimestamp = new Timestamp(System.currentTimeMillis());
                }
                
                // Calculate total profit
                BigDecimal interest = depositAmount.multiply(interestRate)
                                                  .multiply(new BigDecimal(tenure))
                                                  .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal totalProfit = depositAmount.add(interest);
                
                boolean transactionCreated = false;
                
                if ("Withdraw".equals(transactionType)) {
                    // === WITHDRAW ===
                    BigDecimal withdrawAmount = null;
                    if (withdrawAmountStr != null && !withdrawAmountStr.isEmpty()) {
                        withdrawAmount = new BigDecimal(withdrawAmountStr);
                    }
                    
                    // Validate withdrawal amount
                    currentFD = fdDAO.getFixedDepositById(fdID);
                    if (currentFD.getRemainingBalance() != null) {
                        if (withdrawAmount.compareTo(currentFD.getRemainingBalance()) > 0) {
                            session.setAttribute("errorMessage", "Withdrawal exceeds remaining balance!");
                            response.sendRedirect("UpdateFDServlet?id=" + fdID);
                            return;
                        }
                    }
                    
                    if ("Partial".equals(withdrawableStatus)) {
                        BigDecimal currentBalance = currentFD.getRemainingBalance();
                        BigDecimal halfBalance = currentBalance.divide(new BigDecimal(2), 2, BigDecimal.ROUND_DOWN);
                        if (withdrawAmount.compareTo(halfBalance) > 0) {
                            session.setAttribute("errorMessage", "Partial withdrawal max RM " + halfBalance + " (half of balance)!");
                            response.sendRedirect("UpdateFDServlet?id=" + fdID);
                            return;
                        }
                    }
                    
                    transactionCreated = fdDAO.insertFDTransaction_Withdraw(
                        fdID, staffID, transactionTimestamp, withdrawAmount, totalProfit
                    );
                    
                } else if ("Reinvest".equals(transactionType)) {
                    // === REINVEST: Get new FD details from form ===
                    String newStartDateStr = request.getParameter("newStartDate");
                    String newTenureStr = request.getParameter("newTenure");
                    String newMaturityDateStr = request.getParameter("newMaturityDate");
                    
                    System.out.println("🔄 Processing REINVEST for FD" + fdID);
                    System.out.println("  - New Start Date: " + newStartDateStr);
                    System.out.println("  - New Tenure: " + newTenureStr);
                    System.out.println("  - New Maturity Date: " + newMaturityDateStr);
                    
                    // Validate reinvest fields
                    if (newStartDateStr != null && !newStartDateStr.isEmpty() &&
                        newTenureStr != null && !newTenureStr.isEmpty() &&
                        newMaturityDateStr != null && !newMaturityDateStr.isEmpty()) {
                        
                        // Parse reinvest fields
                        java.sql.Date newStartDate = java.sql.Date.valueOf(newStartDateStr);
                        int newTenure = Integer.parseInt(newTenureStr);
                        java.sql.Date newMaturityDate = java.sql.Date.valueOf(newMaturityDateStr);
                        
                        // Call reinvestFD - creates new FD linked to old one
                        int newFdID = fdDAO.reinvestFD(
                            fdID,                   // Old FD being reinvested
                            newStartDate,           // New start date from user
                            newTenure,              // New tenure from user
                            newMaturityDate,        // New maturity date from user
                            transactionTimestamp,   // When reinvestment happened
                            staffID                 // Who processed it
                        );
                        
                        if (newFdID > 0) {
                            System.out.println("✅ REINVESTMENT SUCCESS!");
                            System.out.println("   Old FD: FD" + fdID + " (now MATURED)");
                            System.out.println("   New FD: FD" + newFdID + " (ONGOING)");
                            
                            // Set success flags
                            session.setAttribute("reinvestSuccess", true);
                            session.setAttribute("newFdID", newFdID);
                            session.setAttribute("oldFdID", fdID);
                            transactionCreated = true;
                        } else {
                            System.err.println("❌ REINVESTMENT FAILED for FD" + fdID);
                        }
                        
                    } else {
                        System.err.println("❌ Missing required reinvest fields!");
                    }
                }
                
                if (!transactionCreated) {
                    System.err.println("Warning: Transaction record creation failed for FD" + fdID);
                } else {
                    System.out.println("Transaction/Reinvestment successful for FD" + fdID);
                }
            }
            
            // Set success message
            session.setAttribute("fdUpdateSuccess", true);
            session.setAttribute("updatedFdId", "FD" + fdID);
            
            System.out.println("Fixed Deposit FD" + fdID + " updated successfully!");
            
            // Redirect to FD List
            response.sendRedirect("FDListServlet");
            
        } catch (Exception e) {
            System.err.println("Error updating Fixed Deposit: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("UpdateFD.jsp").forward(request, response);
        }
    }
}
