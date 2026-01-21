package com.fd.servlet;

import com.fd.dao.FixedDepositDAO;
import com.fd.dao.BankDAO;
import com.fd.model.FixedDepositRecord;
import com.fd.model.Bank;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * GenerateReportServlet - Loads FD data and banks for report generation
 */
@WebServlet("/GenerateReportServlet")
public class GenerateReportServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private FixedDepositDAO fdDAO;
    private BankDAO bankDAO;
    
    @Override
    public void init() throws ServletException {
        fdDAO = new FixedDepositDAO();
        bankDAO = new BankDAO();
        System.out.println("========================================");
        System.out.println("✅ GenerateReportServlet initialized!");
        System.out.println("========================================");
    }
    
    /**
     * GET - Load all FD records and banks, forward to GenerateReport.jsp
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("========================================");
        System.out.println("📊 GenerateReportServlet - Loading report data");
        System.out.println("========================================");
        
        try {
            // Load all FD records from database
            List<FixedDepositRecord> fdList = fdDAO.getAllFixedDeposits();
            System.out.println("📋 Loaded " + fdList.size() + " FD records");
            
            // Load all banks for dropdown
            List<Bank> bankList = bankDAO.getAllBanks();
            System.out.println("🏦 Loaded " + bankList.size() + " banks");
            
            // Store in request
            request.setAttribute("fdList", fdList);
            request.setAttribute("bankList", bankList);
            
            System.out.println("✅ Forwarding to GenerateReport.jsp");
            System.out.println("========================================");
            
            // Forward to JSP
            request.getRequestDispatcher("GenerateReport.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error loading report data: " + e.getMessage());
            e.printStackTrace();
            
            // Set empty lists on error
            request.setAttribute("fdList", new ArrayList<FixedDepositRecord>());
            request.setAttribute("bankList", new ArrayList<Bank>());
            request.setAttribute("error", "Unable to load report data. Please try again.");
            
            request.getRequestDispatcher("GenerateReport.jsp").forward(request, response);
        }
    }
}
