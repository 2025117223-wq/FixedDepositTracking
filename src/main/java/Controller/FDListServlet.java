package com.fd.servlet;

import com.fd.dao.FixedDepositDAO;
import com.fd.model.FixedDepositRecord;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * FDListServlet
 * Displays list of all Fixed Deposits
 */
@WebServlet("/FDListServlet")
public class FDListServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private FixedDepositDAO fdDAO;
    
    @Override
    public void init() throws ServletException {
        fdDAO = new FixedDepositDAO();
        System.out.println("FDListServlet initialized successfully!");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        try {
            // Get all Fixed Deposits from database
            List<FixedDepositRecord> fdList = fdDAO.getAllFixedDeposits();
            
            // Set as request attribute
            request.setAttribute("fdList", fdList);
            
            // **FIX: Transfer success messages from session to sessionStorage via request attributes**
            Boolean fdSuccess = (Boolean) session.getAttribute("fdSuccess");
            Integer newFdID = (Integer) session.getAttribute("newFdID");
            Boolean fdUpdateSuccess = (Boolean) session.getAttribute("fdUpdateSuccess");
            String updatedFdId = (String) session.getAttribute("updatedFdId");
            
            if (fdSuccess != null && fdSuccess) {
                request.setAttribute("fdSuccess", "true");
                if (newFdID != null) {
                    request.setAttribute("newFdID", newFdID);
                }
                // Clear from session after transferring
                session.removeAttribute("fdSuccess");
                session.removeAttribute("newFdID");
            }
            
            if (fdUpdateSuccess != null && fdUpdateSuccess) {
                request.setAttribute("fdUpdateSuccess", "true");
                if (updatedFdId != null) {
                    request.setAttribute("updatedFdId", updatedFdId);
                }
                // Clear from session after transferring
                session.removeAttribute("fdUpdateSuccess");
                session.removeAttribute("updatedFdId");
            }
            
            // Forward to FDList.jsp
            request.getRequestDispatcher("FDList.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error retrieving Fixed Deposits: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading Fixed Deposits: " + e.getMessage());
            request.getRequestDispatcher("FDList.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
