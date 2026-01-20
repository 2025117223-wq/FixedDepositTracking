package com.fd.servlet;

import com.fd.dao.BankDAO;
import com.fd.model.Bank;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * BankServlet - Handles all bank-related operations
 * Create, Read, Update operations for banks
 */
@WebServlet("/BankServlet")
public class BankServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BankDAO bankDAO;
    
    @Override
    public void init() {
        bankDAO = new BankDAO();
        System.out.println("========================================");
        System.out.println("✅ BankServlet INITIALIZED (Tomcat 10+)");
        System.out.println("========================================");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        System.out.println("========================================");
        System.out.println("📥 BankServlet - GET Request");
        System.out.println("   Action: " + action);
        System.out.println("========================================");
        
        if (action == null) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "edit":
                    showEditForm(request, response);
                    break;
                case "list":
                default:
                    listBanks(request, response);
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error in BankServlet GET: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("BankList.jsp?error=true");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        
        System.out.println("========================================");
        System.out.println("📥 BankServlet - POST Request");
        System.out.println("   Action: " + action);
        System.out.println("========================================");
        
        if (action == null) {
            response.sendRedirect("BankList.jsp");
            return;
        }
        
        try {
            switch (action) {
                case "create":
                    createBank(request, response);
                    break;
                case "update":
                    updateBank(request, response);
                    break;
                default:
                    response.sendRedirect("BankList.jsp");
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error in BankServlet POST: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("BankList.jsp?error=true");
        }
    }
    
    /**
     * List all banks
     */
    private void listBanks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("📋 Forwarding to BankList.jsp");
        request.getRequestDispatcher("BankList.jsp").forward(request, response);
    }
    
    /**
     * Show edit form for a specific bank
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            System.err.println("❌ Missing bank ID parameter");
            response.sendRedirect("BankList.jsp?error=missing_id");
            return;
        }
        
        try {
            long bankId = Long.parseLong(idStr);  // Use long for bankId
            System.out.println("🔍 Fetching bank for edit: ID " + bankId);
            
            Bank bank = bankDAO.getBankById(bankId);
            
            if (bank == null) {
                System.err.println("❌ Bank not found: ID " + bankId);
                response.sendRedirect("BankList.jsp?error=not_found");
                return;
            }
            
            System.out.println("✅ Bank found, forwarding to EditBank.jsp");
            request.setAttribute("bank", bank);
            request.getRequestDispatcher("EditBank.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid bank ID: " + idStr);
            response.sendRedirect("BankList.jsp?error=invalid_id");
        }
    }
    
    /**
     * Create a new bank
     */
    private void createBank(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String bankName = request.getParameter("bankName");
        String bankAddress = request.getParameter("bankAddress");
        String bankPhone = request.getParameter("bankPhone");
        
        System.out.println("📝 Creating new bank:");
        System.out.println("   Name: " + bankName);
        System.out.println("   Phone: " + bankPhone);
        System.out.println("   Address: " + bankAddress);
        
        // Validation
        if (isBlank(bankName) || isBlank(bankAddress) || isBlank(bankPhone)) {
            System.err.println("❌ Validation failed - Missing required fields");
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("CreateBank.jsp").forward(request, response);
            return;
        }
        
        // Check if bank name already exists
        if (bankDAO.bankNameExists(bankName)) {
            System.err.println("❌ Bank name already exists: " + bankName);
            request.setAttribute("error", "A bank with this name already exists.");
            request.getRequestDispatcher("CreateBank.jsp").forward(request, response);
            return;
        }
        
        // Create bank object
        Bank bank = new Bank(
            bankName.trim(),
            bankAddress.trim(),
            bankPhone.trim()
        );
        
        // Insert into database
        boolean success = bankDAO.insertBank(bank);
        
        if (success) {
            System.out.println("✅ Bank created successfully");
            response.sendRedirect("BankList.jsp?msg=created");
        } else {
            System.err.println("❌ Failed to create bank");
            request.setAttribute("error", "Failed to create bank. Please try again.");
            request.getRequestDispatcher("CreateBank.jsp").forward(request, response);
        }
    }
    
    /**
     * Update an existing bank
     */
    private void updateBank(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("bankId");
        String bankName = request.getParameter("bankName");
        String bankAddress = request.getParameter("bankAddress");
        String bankPhone = request.getParameter("bankPhone");
        
        System.out.println("📝 Updating bank:");
        System.out.println("   ID: " + idStr);
        System.out.println("   Name: " + bankName);
        System.out.println("   Phone: " + bankPhone);
        System.out.println("   Address: " + bankAddress);
        
        // Validation
        if (isBlank(idStr) || isBlank(bankAddress) || isBlank(bankPhone)) {
            System.err.println("❌ Validation failed - Missing required fields");
            
            // Load bank for re-display
            try {
                long bankId = Long.parseLong(idStr);  // Use long for bankId
                Bank bank = bankDAO.getBankById(bankId);
                request.setAttribute("bank", bank);
            } catch (Exception e) {
                // Ignore
            }
            
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("EditBank.jsp").forward(request, response);
            return;
        }
        
        try {
            long bankId = Long.parseLong(idStr);
            
            // Get existing bank to preserve name
            Bank existingBank = bankDAO.getBankById(bankId);
            
            if (existingBank == null) {
                System.err.println("❌ Bank not found: ID " + bankId);
                response.sendRedirect("BankList.jsp?error=not_found");
                return;
            }
            
            // Create updated bank object (name stays the same)
            Bank bank = new Bank(
                bankId,
                existingBank.getBankName(), // Keep original name
                bankAddress.trim(),
                bankPhone.trim()
            );
            
            // Update database
            boolean success = bankDAO.updateBank(bank);
            
            if (success) {
                System.out.println("✅ Bank updated successfully");
                response.sendRedirect("BankList.jsp?msg=updated");
            } else {
                System.err.println("❌ Failed to update bank");
                request.setAttribute("error", "Failed to update bank. Please try again.");
                request.setAttribute("bank", bank);
                request.getRequestDispatcher("EditBank.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid bank ID: " + idStr);
            response.sendRedirect("BankList.jsp?error=invalid_id");
        }
    }
    
    /**
     * Helper method to check if string is blank
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
