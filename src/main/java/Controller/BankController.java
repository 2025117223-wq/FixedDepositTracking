package Controller;

import Bean.Bank;
import DAO.BankDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

@WebServlet("/BankController")
public class BankController extends HttpServlet {
    private BankDAO bankDAO;

    public void init() { bankDAO = new BankDAO(); }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";
        try {
            if ("edit".equals(action)) showEditForm(request, response);
            else listBanks(request, response);
        } catch (SQLException ex) { throw new ServletException(ex); }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("add".equals(action)) insertBank(request, response);
            else if ("update".equals(action)) updateBank(request, response);
        } catch (SQLException ex) { throw new ServletException(ex); }
    }

    private void listBanks(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        request.setAttribute("listBank", bankDAO.getAllBanks());
        request.getRequestDispatcher("BankList.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        request.setAttribute("bank", bankDAO.getBankById(id));
        request.getRequestDispatcher("EditBank.jsp").forward(request, response);
    }

    private void insertBank(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        String name = request.getParameter("bankName");
        Bank bank = new Bank(name, request.getParameter("bankAddress"), request.getParameter("bankPhone"));
        if (bankDAO.insertBank(bank)) {
            response.sendRedirect("BankController?action=list&msg=created&bn=" + URLEncoder.encode(name, "UTF-8"));
        }
    }

    private void updateBank(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("bankId"));
        String name = request.getParameter("bankName");
        Bank bank = new Bank(id, name, request.getParameter("bankAddress"), request.getParameter("bankPhone"));
        if (bankDAO.updateBank(bank)) {
            response.sendRedirect("BankController?action=list&msg=updated&bn=" + URLEncoder.encode(name, "UTF-8"));
        }
    }
}