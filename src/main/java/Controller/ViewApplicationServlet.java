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
import java.text.SimpleDateFormat;

/**
 * ViewApplicationServlet
 * Loads FD data from database and displays it in ApplicationForm.jsp (read-only view)
 */
@WebServlet("/ViewApplicationServlet")
public class ViewApplicationServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private FixedDepositDAO fdDAO;
    
    @Override
    public void init() throws ServletException {
        fdDAO = new FixedDepositDAO();
        System.out.println("========================================");
        System.out.println("✅ ViewApplicationServlet initialized successfully!");
        System.out.println("========================================");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fdIdParam = request.getParameter("id");
        
        System.out.println("========================================");
        System.out.println("📄 ViewApplicationServlet - Loading Application Form");
        System.out.println("   FD ID Parameter: " + fdIdParam);
        System.out.println("========================================");
        
        if (fdIdParam == null || fdIdParam.isEmpty()) {
            System.err.println("❌ Missing FD ID parameter");
            response.sendRedirect("FDListServlet");
            return;
        }
        
        try {
            // Remove "FD" prefix if present
            String fdIdClean = fdIdParam.replace("FD", "").trim();
            int fdID = Integer.parseInt(fdIdClean);
            
            System.out.println("🔍 Looking up FD ID: " + fdID);
            
            // Get FD record from database
            FixedDepositRecord fd = fdDAO.getFixedDepositById(fdID);
            
            if (fd == null) {
                System.err.println("❌ FD not found: " + fdID);
                response.sendRedirect("ViewFDServlet?id=" + fdIdParam);
                return;
            }
            
            System.out.println("✅ FD Record found:");
            System.out.println("   FD ID: FD" + fd.getFdID());
            System.out.println("   Account: " + fd.getAccNumber());
            System.out.println("   Bank: " + fd.getBankName());
            System.out.println("   Amount: RM " + fd.getDepositAmount());
            System.out.println("");
            
            // Format dates for display
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = fd.getStartDate() != null ? dateFormat.format(fd.getStartDate()) : "";
            String maturityDateStr = fd.getMaturityDate() != null ? dateFormat.format(fd.getMaturityDate()) : "";
            
            // Get session and populate with FD data
            HttpSession session = request.getSession();
            
            System.out.println("📝 Populating session with FD data:");
            System.out.println("   Account Number: " + fd.getAccNumber());
            System.out.println("   Bank Name: " + fd.getBankName());
            System.out.println("   Deposit Amount: " + fd.getDepositAmount());
            System.out.println("   Interest Rate: " + fd.getInterestRate());
            System.out.println("   Tenure: " + fd.getTenure());
            System.out.println("   Start Date: " + startDateStr);
            System.out.println("   Maturity Date: " + maturityDateStr);
            
            // Set session attributes for ApplicationForm.jsp
            session.setAttribute("accountNumber", fd.getAccNumber());
            session.setAttribute("referralNumber", fd.getReferralNumber() != null ? fd.getReferralNumber().toString() : "");
            session.setAttribute("bankName", fd.getBankName());
            session.setAttribute("depositAmount", fd.getDepositAmount().toString());
            session.setAttribute("interestRate", fd.getInterestRate().toString());
            session.setAttribute("tenure", String.valueOf(fd.getTenure()));
            session.setAttribute("startDate", startDateStr);
            session.setAttribute("maturityDate", maturityDateStr);
            session.setAttribute("certNo", fd.getCertNo() != null ? fd.getCertNo() : "");
            session.setAttribute("fdType", fd.getFdType());
            
            // Set flag to indicate this is for viewing (not submitting)
            session.setAttribute("fdPendingSubmit", true);
            session.setAttribute("viewMode", true); // NEW: Indicate this is read-only view mode
            session.setAttribute("viewFdID", fdID); // NEW: Store FD ID for back button
            
            System.out.println("✅ Session populated successfully");
            System.out.println("");
            System.out.println("========================================");
            System.out.println("✅ Redirecting to ApplicationForm.jsp");
            System.out.println("========================================");
            
            // Redirect to ApplicationForm.jsp
            response.sendRedirect("ApplicationForm.jsp");
            
        } catch (NumberFormatException e) {
            System.err.println("========================================");
            System.err.println("❌ Invalid FD ID format: " + fdIdParam);
            System.err.println("   Error: " + e.getMessage());
            System.err.println("========================================");
            response.sendRedirect("FDListServlet");
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("❌ Error loading FD for application form: " + e.getMessage());
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
