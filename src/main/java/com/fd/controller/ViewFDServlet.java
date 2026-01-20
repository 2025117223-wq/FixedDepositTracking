package com.fd.servlet;

import com.fd.dao.FixedDepositDAO;
import com.fd.model.FixedDepositRecord;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * ViewFDServlet
 * UPDATED: Loads Fixed Deposit data with proper FREE/PLEDGE FD attribute display
 */
@WebServlet("/ViewFDServlet")
public class ViewFDServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private FixedDepositDAO fdDAO;
    
    @Override
    public void init() throws ServletException {
        fdDAO = new FixedDepositDAO();
        System.out.println("========================================");
        System.out.println("✅ ViewFDServlet initialized successfully!");
        System.out.println("========================================");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fdIdParam = request.getParameter("id");
        
        System.out.println("========================================");
        System.out.println("📥 ViewFDServlet - GET Request");
        System.out.println("   FD ID Parameter: " + fdIdParam);
        System.out.println("========================================");
        
        if (fdIdParam == null || fdIdParam.isEmpty()) {
            System.err.println("❌ Missing FD ID parameter");
            response.sendRedirect("FDListServlet");
            return;
        }
        
        try {
            // Remove "FD" prefix if present (e.g., "FD5" -> "5")
            String fdIdClean = fdIdParam.replace("FD", "").trim();
            int fdID = Integer.parseInt(fdIdClean);
            
            System.out.println("🔍 Looking up FD ID: " + fdID);
            
            // Get FD record from database
            FixedDepositRecord fd = fdDAO.getFixedDepositById(fdID);
            
            if (fd == null) {
                System.err.println("❌ FD not found: " + fdID);
                request.setAttribute("error", "Fixed Deposit not found: FD" + fdID);
                request.getRequestDispatcher("FDListServlet").forward(request, response);
                return;
            }
            
            System.out.println("✅ FD Record found:");
            System.out.println("   FD ID: FD" + fd.getFdID());
            System.out.println("   Account: " + fd.getAccNumber());
            System.out.println("   Type: " + fd.getFdType());
            System.out.println("   Bank: " + fd.getBankName());
            System.out.println("   Amount: RM " + fd.getDepositAmount());
            System.out.println("   Status: " + fd.getStatus());
            System.out.println("");
            
            // ============================================================
            // GET TYPE-SPECIFIC DATA (FREE FD or PLEDGE FD)
            // ============================================================
            
            if ("FREEFD".equals(fd.getFdType())) {
                System.out.println("🔵 Loading FREE FD attributes...");
                Map<String, String> freeData = fdDAO.getFreeFDData(fdID);
                
                if (freeData != null) {
                    String autoRenewal = freeData.get("autoRenewalStatus");
                    String withdrawable = freeData.get("withdrawableStatus");
                    
                    System.out.println("   Auto Renewal Status (DB): " + autoRenewal);
                    System.out.println("   Withdrawable Status (DB): " + withdrawable);
                    
                    // Convert Y/N to Yes/No for display
                    String autoRenewalDisplay = "Y".equals(autoRenewal) ? "Yes" : "No";
                    
                    System.out.println("   Auto Renewal Status (Display): " + autoRenewalDisplay);
                    System.out.println("   Withdrawable Status (Display): " + withdrawable);
                    
                    request.setAttribute("autoRenewalStatus", autoRenewalDisplay);
                    request.setAttribute("withdrawableStatus", withdrawable);
                    
                    System.out.println("✅ FREE FD attributes loaded successfully");
                } else {
                    System.err.println("⚠️ No FREE FD data found for FD" + fdID);
                    request.setAttribute("autoRenewalStatus", "-");
                    request.setAttribute("withdrawableStatus", "-");
                }
                
            } else if ("PLEDGEFD".equals(fd.getFdType())) {
                System.out.println("🟣 Loading PLEDGE FD attributes...");
                Map<String, Object> pledgeData = fdDAO.getPledgeFDData(fdID);
                
                if (pledgeData != null) {
                    String collateral = (String) pledgeData.get("collateralStatus");
                    BigDecimal pledgeValue = (BigDecimal) pledgeData.get("pledgeValue");
                    
                    System.out.println("   Collateral Status (DB): " + collateral);
                    System.out.println("   Pledge Value (DB): " + pledgeValue);
                    
                    // Convert Y/N to Active/Partial for display
                    String collateralDisplay = "Y".equals(collateral) ? "Active" : "Partial";
                    
                    System.out.println("   Collateral Status (Display): " + collateralDisplay);
                    System.out.println("   Pledge Value (Display): RM " + pledgeValue);
                    
                    request.setAttribute("collateralStatus", collateralDisplay);
                    request.setAttribute("pledgeValue", pledgeValue);
                    
                    System.out.println("✅ PLEDGE FD attributes loaded successfully");
                } else {
                    System.err.println("⚠️ No PLEDGE FD data found for FD" + fdID);
                    request.setAttribute("collateralStatus", "-");
                    request.setAttribute("pledgeValue", null);
                }
            }
            
            System.out.println("");
            
            // ============================================================
            // CALCULATE EXPECTED PROFIT AND INTEREST
            // ============================================================
            
            if (fd.getDepositAmount() != null && fd.getInterestRate() != null && fd.getTenure() > 0) {
                System.out.println("💰 Calculating profit...");
                
                // Formula: Interest = (Principal × Rate × Tenure) / (100 × 12)
                BigDecimal interest = fd.getDepositAmount()
                                        .multiply(fd.getInterestRate())
                                        .multiply(new BigDecimal(fd.getTenure()))
                                        .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
                
                // Total Profit = Principal + Interest
                BigDecimal totalProfit = fd.getDepositAmount().add(interest);
                
                DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
                String totalInterestStr = currencyFormat.format(interest);
                String totalProfitStr = currencyFormat.format(totalProfit);
                
                System.out.println("   Principal: RM " + fd.getDepositAmount());
                System.out.println("   Interest Rate: " + fd.getInterestRate() + "%");
                System.out.println("   Tenure: " + fd.getTenure() + " months");
                System.out.println("   Interest: RM " + totalInterestStr);
                System.out.println("   Total Profit: RM " + totalProfitStr);
                
                request.setAttribute("totalInterest", totalInterestStr);
                request.setAttribute("totalProfit", totalProfitStr);
                
                System.out.println("✅ Profit calculated successfully");
            } else {
                System.out.println("⚠️ Cannot calculate profit - missing data");
                request.setAttribute("totalInterest", "-");
                request.setAttribute("totalProfit", "-");
            }
            
            System.out.println("");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📋 VIEW FD SUMMARY:");
            System.out.println("   FD ID: FD" + fd.getFdID());
            System.out.println("   Account: " + fd.getAccNumber());
            System.out.println("   Type: " + fd.getFdType());
            System.out.println("   Bank: " + fd.getBankName());
            System.out.println("   Amount: RM " + fd.getDepositAmount());
            System.out.println("   Status: " + fd.getStatus());
            if ("FREEFD".equals(fd.getFdType())) {
                System.out.println("   Auto Renewal: " + request.getAttribute("autoRenewalStatus"));
                System.out.println("   Withdrawable: " + request.getAttribute("withdrawableStatus"));
            } else if ("PLEDGEFD".equals(fd.getFdType())) {
                System.out.println("   Collateral: " + request.getAttribute("collateralStatus"));
                System.out.println("   Pledge Value: RM " + request.getAttribute("pledgeValue"));
            }
            System.out.println("   Total Interest: RM " + request.getAttribute("totalInterest"));
            System.out.println("   Total Profit: RM " + request.getAttribute("totalProfit"));
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // Set FD data as request attribute
            request.setAttribute("fd", fd);
            
            System.out.println("");
            System.out.println("========================================");
            System.out.println("✅ Forwarding to ViewFD.jsp");
            System.out.println("========================================");
            
            // Forward to ViewFD.jsp
            request.getRequestDispatcher("ViewFD.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            System.err.println("========================================");
            System.err.println("❌ Invalid FD ID format: " + fdIdParam);
            System.err.println("   Error: " + e.getMessage());
            System.err.println("========================================");
            response.sendRedirect("FDListServlet");
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("❌ Error loading FD: " + e.getMessage());
            e.printStackTrace();
            System.err.println("========================================");
            response.sendRedirect("FDListServlet");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect POST to GET
        doGet(request, response);
    }
}
